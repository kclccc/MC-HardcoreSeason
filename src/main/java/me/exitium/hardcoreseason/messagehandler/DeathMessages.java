package me.exitium.hardcoreseason.messagehandler;

import org.apache.commons.text.StringSubstitutor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class DeathMessages {
    Map<String, String> messages = Map.ofEntries(
            entry("death.attack.anvil", "${player} was squashed by a falling anvil"),
            entry("death.attack.arrow", "${player} was shot by ${killer}"),
            entry("death.attack.arrow.item", "${player} was shot by ${killer} using ${item}"),
            entry("death.attack.bullet", "${player} was sniped by ${killer}"),
            entry("death.attack.cactus", "${player} was pricked to death"),
            entry("death.attack.cactus.player", "${player} walked into a cactus whilst trying to escape ${killer}"),
            entry("death.attack.drown", "${player} drowned"),
            entry("death.attack.drown.player", "${player} drowned whilst trying to escape ${killer}"),
            entry("death.attack.explosion", "${player} blew up"),
            entry("death.attack.explosion.player", "${player} was blown up by ${killer}"),
            entry("death.attack.fall", "${player} hit the ground too hard"),
            entry("death.attack.fallingBlock", "${player} was squashed by a falling block"),
            entry("death.attack.fireball", "${player} was fireballed by ${killer}"),
            entry("death.attack.fireball.item", "${player} was fireballed by ${killer} using ${item}"),
            entry("death.attack.fireworks", "${player} went off with a bang"),
            entry("death.attack.flyIntoWall", "${player} experienced kinetic energy"),
            entry("death.attack.generic", "${player} died"),
            entry("death.attack.indirectMagic", "${player} was killed by ${killer} using magic"),
            entry("death.attack.indirectMagic.item", "${player} was killed by ${killer} using ${item}"),
            entry("death.attack.inFire", "${player} went up in flames"),
            entry("death.attack.inFire.player", "${player} walked into fire whilst fighting ${killer}"),
            entry("death.attack.inWall", "${player} suffocated in a wall"),
            entry("death.attack.lava", "${player} tried to swim in lava"),
            entry("death.attack.lava.player", "${player} tried to swim in lava to escape ${killer}"),
            entry("death.attack.lightningBolt", "${player} was struck by lightning"),
            entry("death.attack.magic", "${player} was killed by magic"),
            entry("death.attack.magma", "${player} discovered floor was lava"),
            entry("death.attack.magma.player", "${player} walked on danger zone due to ${killer}"),
            entry("death.attack.mob", "${player} was slain by ${killer}"),
            entry("death.attack.onFire", "${player} burned to death"),
            entry("death.attack.onFire.player", "${player} was burnt to a crisp whilst fighting ${killer}"),
            entry("death.attack.outOfWorld", "${player} fell out of the world"),
            entry("death.attack.player", "${player} was slain by ${killer}"),
            entry("death.attack.player.item", "${player} was slain by ${killer} using ${item}"),
            entry("death.attack.spit", "${player} was spitballed by ${killer}"),
            entry("death.attack.starve", "${player} starved to death"),
            entry("death.attack.thorns", "${player} was killed trying to hurt ${killer}"),
            entry("death.attack.thrown", "${player} was pummeled by ${killer}"),
            entry("death.attack.thrown.item", "${player} was pummeled by ${killer} using ${item}"),
            entry("death.attack.trident", "${player} was impaled to death by ${killer}"),
            entry("death.attack.wither", "${player} withered away"),
            entry("death.fell.accident.generic", "${player} fell from a high place"),
            entry("death.fell.accident.ladder", "${player} fell off a ladder"),
            entry("death.fell.accident.vines", "${player} fell off some vines"),
            entry("death.fell.accident.water", "${player} fell out of the water"),
            entry("death.fell.assist", "${player} was doomed to fall by ${killer}"),
            entry("death.fell.assist.item", "${player} was doomed to fall by ${killer} using ${item}"),
            entry("death.fell.finish", "${player} fell too far and was finished by ${killer}"),
            entry("death.fell.finish.item", "${player} fell too far and was finished by ${killer} using ${item}"),
            entry("death.fell.killer", "${player} was doomed to fall")
    );

    public String interpolate(String key, String player, List<String> keys) {
        Map<String, String> params = new HashMap<>();
        params.put("player", player);
        if (keys.size() == 1) params.put("killer", keys.get(0));
        if (keys.size() > 1) params.put("item", keys.get(1));
        StringSubstitutor s = new StringSubstitutor(params);
        return s.replace(messages.get(key));
    }
}
