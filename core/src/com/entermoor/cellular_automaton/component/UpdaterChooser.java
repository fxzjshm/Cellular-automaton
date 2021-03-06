package com.entermoor.cellular_automaton.component;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.entermoor.cellular_automaton.CellularAutomaton;
import com.entermoor.cellular_automaton.UpdateRateTester;
import com.entermoor.cellular_automaton.updater.AsynchronousUpdater;
import com.entermoor.cellular_automaton.updater.CellPoolUpdater;
import com.entermoor.cellular_automaton.updater.MultiThreadUpdater;
import com.entermoor.cellular_automaton.updater.SingleThreadUpdater;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.kotcrab.vis.ui.widget.VisList;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTextButton;

public class UpdaterChooser extends ApplicationAdapter {
    // TODO UI for swiching updater

    public CellularAutomaton main;
    public UpdateRateTester updateRateTester;
    public VisDialog dialog;
    public ScrollPane scrollPane;
    public List<CellPoolUpdater> list;
    public CellPoolUpdater[] updatersArray = new CellPoolUpdater[0];
    public Button okButton;

    public UpdaterChooser(CellularAutomaton main) {
        this.main = main;
    }

    @Override
    public void create() {
        SingleThreadUpdater singleThreadUpdater = new SingleThreadUpdater();
        main.updaters.add(singleThreadUpdater);
        MultiThreadUpdater multiThreadUpdater = new MultiThreadUpdater();
        main.updaters.add(multiThreadUpdater);

        updateRateTester = new UpdateRateTester(main);

        for (CellPoolUpdater updater : main.updaters) {
            if (updater instanceof AsynchronousUpdater) {
                main.updater = updater;
                break;
            }
        }
        if (null == main.updater || !(main.updaters.contains(main.updater))) {
            if (main.updaters.contains(multiThreadUpdater)) main.updater = multiThreadUpdater;
            else main.updater = singleThreadUpdater;
        }
        Gdx.app.debug("UpdaterChooser", "Chosen " + main.updater.getName());

        dialog = new VisDialog("Choose the cell pool updater");
        dialog.closeOnEscape();
        dialog.addCloseButton();
        list = new VisList<CellPoolUpdater>();
        scrollPane = new VisScrollPane(list);
        dialog.getContentTable().add(scrollPane);
        okButton = new VisTextButton("Yes");
        okButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                CellPoolUpdater newUpdater = list.getSelected();
                if (null != newUpdater) {
                    main.updater = newUpdater;
                    Gdx.app.debug("UpdaterChooser", "Chosen " + newUpdater.getName());
                }
                dialog.fadeOut();
            }
        });
        dialog.getButtonsTable().add(okButton);
    }

    public void showChoosingDialog() {
        updateDialog();
        main.ui.stage.addActor(dialog);
        dialog.fadeIn();
        CellularAutomaton.asyncExecutor.submit(new AsyncTask<Object>() {
            @Override
            public Object call() {
                main.isRunning = false;
                updateRateTester.testUpdateRate();
                main.isRunning = true;
                return null;
            }
        });
    }

    public void updateList() {
        updatersArray = main.updaters.toArray(updatersArray);
        for (CellPoolUpdater updater : updatersArray) {
            updater.updateEntryMessage();
        }
        list.setItems(updatersArray);
    }

    public void updateDialog() {
        updateList();
        scrollPane.updateVisualScroll();
        dialog.pack();
        dialog.centerWindow();
    }
}
