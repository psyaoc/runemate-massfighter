package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Health;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.queries.SpriteItemQueryBuilder;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.rs3.local.hud.Powers;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import com.runemate.game.api.script.framework.task.TaskScript;
import scripts.MassFighter.MassFighter;

import static scripts.MassFighter.MassFighter.settings;

public class PrayerHandler extends Task {

    final SpriteItemQueryBuilder validPrayerItems = Inventory.newQuery().filter(new Filter<SpriteItem>() {
        @Override
        public boolean accepts(SpriteItem spriteItem) {
            String name = spriteItem.getDefinition().getName();
            return name.contains("Prayer potion") ||
                    name.contains("Prayer flask");
        }
    });
    public boolean validate() {
        System.out.println("Trying to activate the prayer handler");
        return settings.useSoulsplit || settings.quickPray &&
                // We need to get more prayer points and we have pots/flasks remaining
                ((Powers.Prayer.getPoints() < settings.prayValue && !validPrayerItems.results().isEmpty())
                // We need to turn soulsplit off as we have enough health now
                || (Powers.Prayer.Curse.SOUL_SPLIT.isActivated() && Health.getCurrentPercent() > 80)
                // We need to turn soulsplit on as we are losing health
                || (!Powers.Prayer.Curse.SOUL_SPLIT.isActivated() && Health.getCurrentPercent() < 65
                        && Powers.Prayer.getPoints() >= settings.prayValue)
                // We need to enable quick prayers
                || (settings.quickPray && !Powers.Prayer.isQuickPraying() && Powers.Prayer.getPoints() >= settings.prayValue));
    }

    @Override
    public void execute() {

        System.out.println("In the prayer handler");

        // turn on quick prayer if it is not on
        if (settings.quickPray && !Powers.Prayer.isQuickPraying() && Powers.Prayer.getPoints() >= settings.prayValue) {
            MassFighter.status = "QuickPrayers: ON";
            if (Powers.Prayer.toggleQuickPrayers()) {
                Execution.delayUntil(Powers.Prayer::isQuickPraying, 1600, 2000);
            }
        }

        // Disable soulsplit if necessary
        if (Powers.Prayer.Curse.SOUL_SPLIT.isActivated() && Health.getCurrentPercent() > 80) {
            MassFighter.status = "Sousplit: OFF";
            if (Powers.Prayer.Curse.SOUL_SPLIT.toggle()) {
                Execution.delayUntil(() -> !Powers.Prayer.Curse.SOUL_SPLIT.isActivated(), 1600,2000);
            }
        }

        // Enable soulsplit if necessary
        if (!Powers.Prayer.Curse.SOUL_SPLIT.isActivated() && Health.getCurrentPercent() < 65
                && Powers.Prayer.getPoints() >= settings.prayValue) {
            MassFighter.status = "Soulsplit: ON";
            if (Powers.Prayer.Curse.SOUL_SPLIT.toggle()) {
                Execution.delayUntil(Powers.Prayer.Curse.SOUL_SPLIT::isActivated, 1600,2000);
            }
        }

        // Drinks a prayer pot/flask (starting with flasks)in order to restore prayer points
        // At the moment this occurs if prayer points fall below 50% of the maximum possible amount of points
        // Delays until prayer points have increased or 2s pass
        if (Powers.Prayer.getPoints() < settings.prayValue) {
            if (validPrayerItems.results().isEmpty()) {
                if (settings.exitOnPrayerOut) {
                    MassFighter.methods.logout();
                } else {
                    settings.useSoulsplit = false;
                    settings.quickPray = false;
                    System.out.println("Trying to remove Prayer Handler");
                    TaskScript rootScript = (TaskScript)Environment.getScript();
                    rootScript.getTasks().stream().filter(task -> task != null && task instanceof PrayerHandler).forEach(task -> {
                        System.out.println("Removed Prayer Handler");
                        rootScript.remove(task);
                    });
                }
            } else {
                MassFighter.status = "Getting Prayer";
                final int startPP = Powers.Prayer.getPoints();
                final SpriteItem targetPrayerFuel = validPrayerItems.results().random();
                if (targetPrayerFuel != null) {
                    if (targetPrayerFuel.interact("Drink", targetPrayerFuel.getDefinition().getName())) {
                        Execution.delayUntil(() -> Powers.Prayer.getPoints() > startPP, Random.nextInt(1600, 2000));
                    }
                }
            }
        }
    }
}
