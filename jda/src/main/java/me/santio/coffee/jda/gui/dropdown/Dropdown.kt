package me.santio.coffee.jda.gui.dropdown

import me.santio.coffee.common.annotations.ParserIgnore
import me.santio.coffee.common.registry.AdapterRegistry.toBoxed
import me.santio.coffee.common.resolvers.AnnotationResolver
import me.santio.coffee.common.resolvers.IDResolver
import me.santio.coffee.common.resolvers.NameResolver
import me.santio.coffee.common.resolvers.Scope
import me.santio.coffee.jda.gui.button.ButtonManager
import me.santio.coffee.jda.gui.dropdown.annotations.Option
import net.dv8tion.jda.api.entities.IMentionable
import net.dv8tion.jda.api.interactions.components.selections.*
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu.SelectTarget
import java.lang.reflect.Field
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.function.Consumer
import kotlin.concurrent.schedule
import kotlin.math.max

@Suppress("unused")
class Dropdown <T: Any> private constructor(
    private val options: List<SelectOption>,
    private val callback: Consumer<DropdownContext<*>>,
    private val selectTarget: SelectTarget? = null,
    expiry: Duration = Duration.of(1, ChronoUnit.MINUTES)
) {
    internal val id: String = IDResolver.id()
    private val enabled: MutableList<String> = mutableListOf()

    private var clazz: Class<T>? = null
    private var multiple = false
    private var disabled = false
    private var placeholder: String? = null
    private var onExpire: Consumer<Dropdown<T>>? = null

    init {
        DropdownManager.dropdowns.add(this)

        // Invalidate the button after 1 minute to prevent memory leaks
        Timer().schedule(expiry.toMillis()) {
            if (ButtonManager.get(id) == null) return@schedule
            ButtonManager.unregister(id)
            onExpire?.accept(this@Dropdown)
        }
    }

    /**
     * Builds a JDA select menu from the options provided in the dropdown.
     * @return the built select menu
     * @see StringSelectMenu
     * @see EntitySelectMenu
     */
    fun build(): SelectMenu {
        val menu = when (selectTarget) {
            null -> {
                StringSelectMenu.create(id).addOptions(options).setDefaultOptions(
                    *options.filter { enabled.contains(it.value) }.toTypedArray()
                )
            }
            else -> EntitySelectMenu.create(id, selectTarget)
        }

        if (multiple) menu.setRequiredRange(1, max(options.size, 1))
        if (placeholder != null) menu.setPlaceholder(placeholder)
        menu.setDisabled(disabled)

        return menu.build()
    }

    @Suppress("UNCHECKED_CAST", "NAME_SHADOWING")
    fun execute(event: SelectMenuInteraction<*, *>) {
        val context = DropdownContext<Any>(this, event)

        if (clazz != null && event is StringSelectInteraction) {
            val constructor = clazz!!.getDeclaredConstructor()
            constructor.isAccessible = true

            val instance = constructor.newInstance()

            for (field in getAvailableFields(clazz!!)) {
                field.set(instance, event.selectedOptions.any {
                    it.value == field.name
                })
            }

            context.selected = instance as T
        } else if (event is StringSelectInteraction) {
            val context = context as DropdownContext<List<String>>
            context.selected = event.selectedOptions.map {
                it.value
            }
        } else if (event is EntitySelectInteraction) {
            val context = context as DropdownContext<List<IMentionable>>
            context.selected = event.mentions.getMentions()
        }

        callback.accept(context)
    }

    /**
     * Whether the dropdown allows for multiple options to be selected.
     * @param multiple whether the dropdown should allow multiple values
     * @return the dropdown
     */
    fun multiple(multiple: Boolean = true): Dropdown<T> {
        this.multiple = multiple
        return this
    }

    /**
     * Sets the placeholder shown when no options are currently selected
     * @param placeholder the placeholder
     * @return the dropdown
     */
    fun placeholder(placeholder: String): Dropdown<T> {
        this.placeholder = placeholder
        return this
    }

    /**
     * Disables (or enables) the dropdown, disallowing clicks but still showing the placeholder
     * message to users.
     * @param disabled whether the dropdown should be disabled
     * @param message the message to show when the dropdown is disabled (overrides the placeholder)
     * @return the dropdown
     */
    fun disable(disabled: Boolean = true, message: String? = null): Dropdown<T> {
        this.disabled = disabled
        if (message != null) this.placeholder = message

        if (disabled) DropdownManager.unregister(id)
        else DropdownManager.dropdowns.add(this)

        return this
    }

    /**
     * Add a consumer for when the dropdown expires. This will always run unless the dropdown
     * was removed when pressed OR if it's currently disabled.
     * @param onExpire the consumer to run when the dropdown expires
     * @return This button
     */
    fun onExpire(onExpire: Consumer<Dropdown<T>>): Dropdown<T> {
        this.onExpire = onExpire
        return this
    }

    @Suppress("UNCHECKED_CAST")
    companion object {
        internal fun getAvailableFields(clazz: Class<*>): List<Field> {
            return clazz.declaredFields.mapNotNull {
                it.isAccessible = true

                if (toBoxed(it.type) != toBoxed(Boolean::class.java) || AnnotationResolver.hasAnnotation(
                    it,
                    ParserIgnore::class.java
                )) return@mapNotNull null

                it
            }
        }

        fun from(vararg options: String, callback: Consumer<DropdownContext<List<String>>>): Dropdown<Unit> {
            return Dropdown(options.map {
                SelectOption.of(it, it)
            }, callback as Consumer<DropdownContext<*>>)
        }

        fun from(vararg options: SelectOption, callback: Consumer<DropdownContext<List<String>>>): Dropdown<Unit> {
            return Dropdown(options.toList(), callback as Consumer<DropdownContext<*>>)
        }

        fun from(selectTarget: SelectTarget, callback: Consumer<DropdownContext<List<IMentionable>>>): Dropdown<Unit> {
            return Dropdown(emptyList(), callback as Consumer<DropdownContext<*>>, selectTarget)
        }

        fun <T: Any> from(clazz: Class<T>, callback: Consumer<DropdownContext<T>>): Dropdown<T> {
            val options = getAvailableFields(clazz).map {
                val annotation = AnnotationResolver.getAnnotation(it, Option::class.java, Scope.SELF)
                SelectOption.of(
                    if (annotation == null || annotation.name.isEmpty()) NameResolver.generateName(it)
                    else annotation.name,
                    it.name
                )
            }

            val dropdown = Dropdown<T>(options, callback as Consumer<DropdownContext<*>>, null)
            dropdown.clazz = clazz

            return dropdown
        }
    }
}
