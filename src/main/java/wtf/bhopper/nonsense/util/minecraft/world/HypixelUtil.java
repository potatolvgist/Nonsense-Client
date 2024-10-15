package wtf.bhopper.nonsense.util.minecraft.world;

import java.util.regex.Pattern;

public class HypixelUtil {

    public static final Pattern[] AUTO_GG_REGEX = {
            Pattern.compile("^ +1st Killer - ?\\\\[?\\\\w*\\\\+*\\\\]? \\\\w+ - \\\\d+(?: Kills?)?$"),
            Pattern.compile("^ *1st (?:Place ?)?(?:-|:)? ?\\[?\\w*\\+*\\]? \\w+(?: : \\d+| - \\d+(?: Points?)?| - \\d+(?: x .)?| \\(\\w+ .{1,6}\\) - \\d+ Kills?|: \\d+:\\d+| - \\d+ (?:Zombie )?(?:Kills?|Blocks? Destroyed)| - \\[LINK\\])?$"),
            Pattern.compile("^ +Winn(?:er #1 \\(\\d+ Kills\\): \\w+ \\(\\w+\\)|er(?::| - )(?:Hiders|Seekers|Defenders|Attackers|PLAYERS?|MURDERERS?|Red|Blue|RED|BLU|\\w+)(?: Team)?|ers?: ?\\[?\\w*\\+*\\]? \\w+(?:, ?\\[?\\w*\\+*\\]? \\w+)?|ing Team ?[\\:-] (?:Animals|Hunters|Red|Green|Blue|Yellow|RED|BLU|Survivors|Vampires))$"),
            Pattern.compile("^ +Alpha Infected: \\\\w+ \\\\(\\\\d+ infections?\\\\)$"),
            Pattern.compile("^ +Murderer: \\\\w+ \\\\(\\\\d+ Kills?\\\\)$"),
            Pattern.compile("^ +You survived \\d+ rounds!$"),
            Pattern.compile("^ +(?:UHC|SkyWars|Bridge|Sumo|Classic|OP|MegaWalls|Bow|NoDebuff|Blitz|Combo|Bow Spleef) (?:Duel|Doubles|3v3|4v4|Teams|Deathmatch|2v2v2v2|3v3v3v3)? ?- \\d+:\\d+$"),
            Pattern.compile("^ +They captured all wools!$"),
            Pattern.compile("^ +Game over!$"),
            Pattern.compile("^ +[\\d\\.]+k?/[\\d\\.]+k? \\w+$"),
            Pattern.compile("^ +(?:Criminal|Cop)s won the game!$"),
            Pattern.compile("^ +\\[?\\w*\\+*\\]? \\w+ - \\d+ Final Kills$"),
            Pattern.compile("^ +Zombies - \\d*:?\\d+:\\d+ \\(Round \\d+\\)$"),
            Pattern.compile("^ +. YOUR STATISTICS .$"),
            Pattern.compile("^ {36}Winner(s?)$"),
            Pattern.compile("^ {21}Bridge CTF [a-zA-Z]+ - \\d\\d:\\d\\d$"),
    };

}
