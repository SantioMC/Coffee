package me.santio.coffee.jda

import me.santio.coffee.common.Coffee
import me.santio.coffee.common.models.CoffeeBundle
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData

/**
 * This registers a global option to every single command for people
 * who have specific edge cases, these options will not be accessible through
 * the parameter list and will instead require a custom implementation of the
 * [CoffeeBundle#handleParameter] method
 * @param option The JDA option data
 * @return The Coffee object instance
 */
fun Coffee.registerGlobalOption(option: OptionData): Coffee {
    JDACommand.registerGlobalOption(option)
    return this
}

/**
 * This will unregister an already-defined global option
 * @param option The JDA option data
 * @return The Coffee object instance
 * @see registerGlobalOption
 */
fun Coffee.removeGlobalOption(option: OptionData): Coffee {
    JDACommand.removeGlobalOption(option)
    return this
}