package me.pikamug.questsexpansion;

import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.blackvein.quests.Quest;
import me.blackvein.quests.Quests;
import me.blackvein.quests.util.Lang;
import me.blackvein.quests.util.MiscUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class QuestsExpansion extends PlaceholderExpansion {

    private Quests plugin;

    /**
     * Since this expansion requires api access to the plugin "Quests" 
     * we must check if said plugin is on the server or not.
     *
     * @return true or false depending on if the required plugin is installed.
     */
    @Override
    public boolean canRegister() {
        return Bukkit.getPluginManager().getPlugin("Quests") != null;
    }

    /**
     * We can optionally override this method if we need to initialize variables 
     * within this class if we need to or even if we have to do other checks to 
     * ensure the hook is properly set up.
     *
     * @return true or false depending on if it can register.
     */
    @Override
    public boolean register() {
  
        // Make sure "Quests" is on the server
        if (!canRegister()) {
            return false;
        }
 
        /*
         * "Quests" does not have static methods to access its api so we must 
         * create a variable to obtain access to it.
         */
        plugin = (Quests) Bukkit.getPluginManager().getPlugin("Quests");
  
        // if for some reason we can not get our variable, we should return false.
        if (plugin == null) {
            return false;
        }

        /*
         * Since we override the register method, we need to call the super method to actually
         * register this hook
         */
        return super.register();
    }

    /**
     * The name of the person who created this expansion should go here.
     * 
     * @return The name of the author as a String.
     */
    @Override
    public String getAuthor() {
        return "PikaMug";
    }
 
    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest 
     * method to obtain a value if a placeholder starts with our 
     * identifier.
     * <br>This must be unique and can not contain % or _
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public String getIdentifier() {
        return "quests";
    }
  
    /**
     * if the expansion requires another plugin as a dependency, the 
     * proper name of the dependency should go here.
     * <br>Set this to {@code null} if your placeholders do not require 
     * another plugin to be installed on the server for them to work.
     * <br>
     * <br>This is extremely important to set your plugin here, since if 
     * you don't do it, your expansion will throw errors.
     *
     * @return The name of our dependency.
     */
    @Override
    public String getRequiredPlugin() {
        return "Quests";
    }

    /**
     * This is the version of this expansion.
     * <br>You don't have to use numbers, since it is set as a String.
     *
     * @return The version as a String.
     */
    @Override
    public String getVersion() {
        return "1.3";
    }
  
    /**
     * This is the method called when a placeholder with our identifier 
     * is found and needs a value.
     * <br>We specify the value identifier in this method.
     * <br>Since PAPI version 2.9.1 can you use OfflinePlayers in your requests.
     *
     * @param  player
     *         A {@link org.bukkit.Player Player}.
     * @param  identifier
     *         A String containing the identifier/value.
     *
     * @return possibly-null String of the requested identifier.
     */
    @Override
    public String onPlaceholderRequest(final Player player, final String identifier) {
        if (player == null) {
            return "";
        }
        
        final String plannerPlaceholder = getPlannerPlaceholder(identifier);
        if (plannerPlaceholder != null) {
            return plannerPlaceholder;
        }
        
        final String playerPlaceholder = getPlayerPlaceholder(player, identifier);
        if (playerPlaceholder != null) {
            return playerPlaceholder;
        }
        
        return null;
    }
    
    private String getPlannerPlaceholder(final String identifier) {
        final Quest quest = matchQuest(identifier.substring(identifier.lastIndexOf("_") + 1));
        if (identifier.startsWith("planner_start_time_")) {
            return MiscUtil.getTime(quest.getPlanner().getStartInMillis());
        }
        if (identifier.startsWith("planner_end_time_")) {
            return MiscUtil.getTime(quest.getPlanner().getEndInMillis());
        }
        if (identifier.startsWith("planner_duration_")) {
            final long duration = quest.getPlanner().getEndInMillis() - quest.getPlanner().getStartInMillis();
            return MiscUtil.getTime(duration);
        }
        if (identifier.startsWith("planner_repeat_cycle_")) {
            return MiscUtil.getTime(quest.getPlanner().getRepeat());
        }
        return null;
    }
    
    @SuppressWarnings("deprecation")
    private String getPlayerPlaceholder(final Player player, final String identifier) {
        if (identifier.equals("player_quest_points")) {
            return String.valueOf(plugin.getQuester(player.getUniqueId()).getQuestPoints());
        }
        if (identifier.equals("player_has_journal")) {
            return String.valueOf(plugin.getQuester(player.getUniqueId()).hasJournal);
        }
        if (identifier.equals("player_current_quest_amount")) {
            return String.valueOf(plugin.getQuester(player.getUniqueId()).getCurrentQuests().size());
        }
        if (identifier.equals("player_completed_quest_amount")) {
            return String.valueOf(plugin.getQuester(player.getUniqueId()).getCompletedQuests().size());
        }
        if (identifier.equals("player_current_quest_names")) {
            String list = "";
            boolean first = true;
            for (final Entry<Quest, Integer> set : plugin.getQuester(player.getUniqueId()).getCurrentQuests().entrySet()) {
                if (!first) {
                    list += "\n";
                }
                first = false;
                list += set.getKey().getName();
            }
            return list;
        }
        if (identifier.equals("player_completed_quest_names")) {
            String list = "";
            boolean first = true;
            for (final String s : plugin.getQuester(player.getUniqueId()).getCompletedQuests()) {
                if (!first) {
                    list += "\n";
                }
                first = false;
                list += s;
            }
            return list;
        }
        if (identifier.startsWith("player_current_objectives_")) {
            final Quest quest = matchQuest(identifier.substring(identifier.lastIndexOf("_") + 1));
            String list = "";
            boolean first = true;
            for (final String s : plugin.getQuester(player.getUniqueId()).getCurrentObjectives(quest, false)) {
                if (!first) {
                    list += "\n";
                }
                first = false;
                list += s;
            }
            return list;
        }
        if (identifier.startsWith("player_has_current_quest_")) {
            final Quest quest = matchQuest(identifier.substring(identifier.lastIndexOf("_") + 1));
            for (final Entry<Quest, Integer> set : plugin.getQuester(player.getUniqueId()).getCurrentQuests().entrySet()) {
                if (set.getKey().getName().equals(quest.getName())) {
                    return Lang.get("yesWord");
                }
            }
            return Lang.get("noWord");
        }
        if (identifier.startsWith("player_has_completed_quest_")) {
            final Quest quest = matchQuest(identifier.substring(identifier.lastIndexOf("_") + 1));
            return String.valueOf(plugin.getQuester(player.getUniqueId()).getCompletedQuests().contains(quest.getName()));
        }
        if (identifier.startsWith("player_cooldown_time_remaining_")) {
            final Quest quest = matchQuest(identifier.substring(identifier.lastIndexOf("_") + 1));
            if (plugin.getQuester(player.getUniqueId()).getCompletedQuests().contains(quest.getName())) {
                return MiscUtil.getTime(plugin.getQuester(player.getUniqueId()).getCooldownDifference(quest));
            }
            return "";
        }
        if (identifier.startsWith("player_current_stage_number_")) {
            final Quest quest = matchQuest(identifier.substring(identifier.lastIndexOf("_") + 1));
            for (final Entry<Quest, Integer> set : plugin.getQuester(player.getUniqueId()).getCurrentQuests().entrySet()) {
                if (set.getKey().getName().equals(quest.getName())) {
                    return String.valueOf(set.getValue() + 1);
                }
            }
            return "";
        }
        if (identifier.startsWith("player_can_accept_quest_")) {
            final Quest quest = matchQuest(identifier.substring(identifier.lastIndexOf("_") + 1));
            return String.valueOf(plugin.getQuester(player.getUniqueId()).canAcceptOffer(quest, false));
        }
        if (identifier.startsWith("player_meets_requirements_to_start_")) {
            final Quest quest = matchQuest(identifier.substring(identifier.lastIndexOf("_") + 1));
            return String.valueOf(quest.testRequirements(plugin.getQuester(player.getUniqueId())));
        }
        return null;
    }
    
    private Quest matchQuest(final String toMatch) {
        Quest quest = plugin.getQuest(toMatch);
        if (quest == null) {
            quest = plugin.getQuestById(toMatch);
        }
        return quest;
    }
}