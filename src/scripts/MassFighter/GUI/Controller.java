package scripts.MassFighter.GUI;

import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.local.hud.interfaces.Health;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.queries.NpcQueryBuilder;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import scripts.MassFighter.Data.Food;
import scripts.MassFighter.Framework.CombatProfile;
import scripts.MassFighter.MassFighter;
import scripts.MassFighter.Profiles.Powerfighting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public class Controller {

    public ListView<String> availableMonsters;
    public ListView<String> selectedLoot;
    public Button addLoot;
    public Button removeLoot;
    public TextField lootName;
    public CheckBox lootCharms;
    public ListView<String> selectedMonsters;
    public TextField eatValue;
    public TextField tileRange;
    public ListView<Food> foodSelection;
    public CheckBox abilities;
    public CheckBox soulsplit;
    public CheckBox stopWhenOutOfFood;
    public CheckBox lootInCombat;
    public CheckBox buryBones;
    public Slider targetSlider;
    public CheckBox waitLoot;
    public ChoiceBox<CombatProfile> profileSelector;
    public Button npcButton;
    public Button refreshButton;
    public Tab lootTab;
    public Label availableMonstersLabel;
    public Label profileStatus;
    public TextField criticalHitpoints;
    public Button btnStart;

    private List<String> getAvailableMonsters(Area area, String action) {
        List<String> availableNpcs = new ArrayList<>();
        NpcQueryBuilder getNearbyNpcs = Npcs.newQuery().within(area).actions("Attack");
        Collection<Npc> npcs = getNearbyNpcs.results();
        if (!npcs.isEmpty()) {
            npcs.stream().filter(n -> n != null && !availableNpcs.contains(n.getName())).forEach(n -> {
                availableNpcs.add(n.getName());
            });
        }
        return availableNpcs;
    }

    // Initial setup
    public void initialize() {

        // Add onAction event handlers
        refreshButton.setOnAction(event -> {
            availableMonsters.getItems().remove(0, availableMonsters.getItems().size());
            final Player player = Players.getLocal();
            if (player != null) {
                availableMonsters.getItems().addAll(getAvailableMonsters(new Area.Circular(player.getPosition(), 20), "Attack"));
            }
        });
        npcButton.setOnAction(event -> {
            if (!selectedMonsters.getSelectionModel().getSelectedItems().isEmpty()) {
                selectedMonsters.getItems().removeAll(selectedMonsters.getSelectionModel().getSelectedItems());
            } else if (!availableMonsters.getSelectionModel().getSelectedItems().isEmpty()) {
                availableMonsters.getSelectionModel().getSelectedItems().stream().filter(s -> !selectedMonsters.getItems().contains(s)).forEach(s -> {
                    selectedMonsters.getItems().add(s);
                });
            }
        });
        addLoot.setOnAction(this::lootChange);
        removeLoot.setOnAction(this::lootChange);
        lootCharms.setOnAction(this::lootChange);
        lootInCombat.setOnAction(this::lootChange);
        buryBones.setOnAction(this::lootChange);
        waitLoot.setOnAction(this::lootChange);
        btnStart.setOnAction(this::start);

        // Init fields
        profileSelector.getItems().addAll(CombatProfile.getProfiles());
        profileSelector.getSelectionModel().select(0);
        foodSelection.getItems().addAll(Food.values());
        tileRange.setText("20");
        eatValue.setText(Integer.toString(Health.getMaximum()/2));
        criticalHitpoints.setText("1000");
        profileChanged();
        profileStatus.setText("Thanks for using MassFighter, please report any issues you have - Ozzy");
    }

    public void profileChanged() {
        profileSelector.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!(observable.getValue() instanceof Powerfighting)) {
                togglePowerfighting(true);
            } else {
                togglePowerfighting(false);
            }
        });
    }

    public void togglePowerfighting(boolean disable) {
        availableMonsters.setDisable(disable);
        selectedMonsters.setDisable(disable);
        refreshButton.setDisable(disable);
        npcButton.setDisable(disable);
        lootTab.setDisable(disable);
        if (disable) {
            profileStatus.setText("Powerfighting features disabled, swap to Powerfighting to re-enable");
        } else {
            profileStatus.setText("Thanks for using MassFighter, please report any issues you have - Ozzy");
        }
    }


    // Start button pressed, start the script
    public void start(ActionEvent actionEvent) {
        if (Pattern.matches("\\d+", eatValue.getText()) && Pattern.matches("\\d+", tileRange.getText()) && Pattern.matches("\\d+", criticalHitpoints.getText())) {
            if (!profileSelector.getSelectionModel().isEmpty()) {
                if (profileSelector.getSelectionModel().getSelectedItem() instanceof Powerfighting && !selectedMonsters.getItems().isEmpty()) {
                    Powerfighting profile = new Powerfighting();
                    if (!selectedLoot.getItems().isEmpty()) {
                        String[] lootNames = selectedLoot.getItems().toArray(new String[selectedLoot.getItems().size()]);
                        profile.setLootNames(lootNames);
                        MassFighter.looting = true;
                    }
                    String[] npcNames = selectedMonsters.getItems().toArray(new String[selectedMonsters.getItems().size()]);
                    profile.setNpcNames(npcNames);
                    List<Area> areas = new ArrayList<>();
                    areas.add(new Area.Circular(Players.getLocal().getPosition(), Double.valueOf(tileRange.getText())));
                    profile.setFightAreas(areas);
                    MassFighter.combatProfile = profile;
                } else if (!(profileSelector.getSelectionModel().getSelectedItem() instanceof Powerfighting)) {
                    MassFighter.combatProfile = profileSelector.getSelectionModel().getSelectedItem();
                    MassFighter.looting = MassFighter.combatProfile.getLootNames() != null;
                }
                if (MassFighter.combatProfile != null) {
                    if (!foodSelection.getSelectionModel().isEmpty()) {
                        MassFighter.useFood = true;
                        MassFighter.food = foodSelection.getSelectionModel().getSelectedItem();
                        MassFighter.eatValue = Integer.valueOf(eatValue.getText());
                    } else {
                        MassFighter.useFood = false;
                    }
                    MassFighter.waitForLoot = waitLoot.isSelected();
                    MassFighter.targetSelection = (int) targetSlider.getValue();
                    MassFighter.lootInCombat = lootInCombat.isSelected();
                    MassFighter.useAbilities = abilities.isSelected();
                    MassFighter.useSoulsplit = soulsplit.isSelected();
                    MassFighter.fightRadius = Integer.valueOf(tileRange.getText());
                    MassFighter.criticalHitpoints = Integer.valueOf(criticalHitpoints.getText());
                    MassFighter.exitOutFood = stopWhenOutOfFood.isSelected();
                    MassFighter.buryBones = buryBones.isSelected();
                    MassFighter.setupRunning = false;
                    closeUI();
                }
            }
        } else {
            System.out.println("Invalid value ranges");
            eatValue.setText("3000");
            tileRange.setText("20");
            criticalHitpoints.setText("1000");
        }
    }

    public void lootChange(ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(addLoot)) {
            String itemName = lootName.getText();
            if (!itemName.isEmpty() && !selectedLoot.getItems().contains(itemName)) {
                selectedLoot.getItems().add(itemName);
            }
        } else if (actionEvent.getSource().equals(removeLoot)) {
            if (!selectedLoot.getSelectionModel().getSelectedItems().isEmpty()) {
                selectedLoot.getItems().removeAll(selectedLoot.getSelectionModel().getSelectedItems());
            }
        } else if (actionEvent.getSource().equals(lootCharms)) {
            String[] charms = {"Crimson charm", "Gold charm", "Blue charm", "Green charm", "Elder charm"};
            for (String s : charms) {
                if (!selectedLoot.getItems().contains(s)) {
                    selectedLoot.getItems().add(s);
                }
            }
        }
    }

    public void closeUI() {
        Stage stage = (Stage) abilities.getScene().getWindow();
        stage.close();
    }
}