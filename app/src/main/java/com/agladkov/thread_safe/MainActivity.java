package com.agladkov.thread_safe;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class MainActivity extends AppCompatActivity {
    final static int deadlockState = 0;
    final static int raceState = 1;
    final static int priorityState = 2;
    final static int volatileState = 3;

    EditText textConsole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textConsole = findViewById(R.id.textConsole);
        Button btnStart = findViewById(R.id.btnStart);
        final RadioButton rbDeadlock = findViewById(R.id.rbDeadlock);
        final RadioButton rbRaceCondition = findViewById(R.id.rbRace);
        final RadioButton rbPriority = findViewById(R.id.rbPriority);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textConsole.setText("");
                if (rbDeadlock.isChecked()) {
                    startDeadlock();
                } else if (rbRaceCondition.isChecked()) {
                    startRaceCondition();
                } else if (rbPriority.isChecked()) {
                    startPriority();
                } else {
                    startVolatile();
                }
            }
        });
    }

    private void startVolatile() {
        ColaLine colaLine = new ColaLine(this, volatileState);
        SpriteLine spriteLine = new SpriteLine(this, volatileState);

        colaLine.start();
        spriteLine.start();
    }

    private void startDeadlock() {
        ColaLine colaLine = new ColaLine(this, deadlockState);
        SpriteLine spriteLine = new SpriteLine(this, deadlockState);

        colaLine.start();
        spriteLine.start();
    }

    private void startRaceCondition() {
        ColaLine colaLine = new ColaLine(this, raceState);
        SpriteLine spriteLine = new SpriteLine(this, raceState);

        colaLine.start();
        spriteLine.start();
    }

    private void startPriority() {
        ColaLine colaLine = new ColaLine(this, priorityState);
        SpriteLine spriteLine = new SpriteLine(this, priorityState);

        spriteLine.start();

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        colaLine.start();
    }

    @SuppressLint("SetTextI18n")
    private void addLine(final String text) {
        textConsole.post(new Runnable() {
            @Override
            public void run() {
                textConsole.setText(textConsole.getText() + text + "\n");
            }
        });
    }

    final static Object Manager1 = new Object();
    final static Object Manager2 = new Object();
    final static Object sortStation = new Object();
    static int canCount = 0;

    static class ColaLine extends Thread {
        private MainActivity activity;
        private int operation;

        ColaLine(MainActivity activity, int operation) {
            this.activity = activity;
            this.operation = operation;
        }

        @Override
        public void run() {
            switch (operation) {
                case volatileState:
                    int localCount = canCount;
                    while (localCount < 6) {
                        if (localCount != canCount) {
//                            activity.addLine("Got update for can count " + canCount);
                            localCount = canCount;
                        }
                    }

                    Log.e("TAG", "cycle finished " + localCount + " can count " + canCount);
//                    activity.addLine("cycle finished");
                    break;

                case deadlockState:
                    synchronized (Manager1) {
                        activity.addLine("Cola line is holding manager 1...");

                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException ignored) {

                        }
                        activity.addLine("Cola line is waiting for manager 2...");

                        synchronized (Manager2) {
                            activity.addLine("Cola line is holding manager 1 and 2");
                        }
                    }
                    break;

                case raceState:
                    activity.addLine("Cola line starts to create cans");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    activity.addLine("Cola line added cans to sortStation");
                    for (int i = 0; i < 20; i++) {
                        if (canCount >= 20) break;

                        canCount++;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        activity.addLine((i + 1) + " can with cola added to card, cans count " + canCount);
                    }
                    break;

                case priorityState:
                    activity.addLine("20 cans of coca cola delivered to sort station");
                    synchronized (sortStation) {
                        activity.addLine("Coca-cola line is lock sort station");


                        for (int i = 0; i < 20; i++) {
                            activity.addLine("Sort station is packing... " + (i + 1) + " can of cola is packing");
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    activity.addLine("Coca-cola packing is finished");
                    }
                    break;
            }
        }
    }

    static class SpriteLine extends Thread {
        private MainActivity activity;
        private int operation;

        SpriteLine(MainActivity activity, int operation) {
            this.activity = activity;
            this.operation = operation;
        }

        @Override
        public void run() {
            switch (operation) {
                case volatileState:
                    int localCount = canCount;
                    while (canCount < 6) {
                        canCount = ++localCount;
                        activity.addLine("Incrementing can count " + canCount);

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case deadlockState:
                    synchronized (Manager2) {
//                        activity.addLine("Sprite line is holding manager 2...");

                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException ignored) {

                        }
                        activity.addLine("Sprite line is waiting for manager 1...");

                        synchronized (Manager1) {
                            activity.addLine("Sprite line is holding manager 1 and 2");
                        }
                    }
                    break;

                case raceState:
                    activity.addLine("Sprite line starts to create cans");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    activity.addLine("Sprite line added cans to sortStation");
                    for (int i = 0; i < 20; i++) {
                        if (canCount >= 20) break;

                        canCount++;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        activity.addLine((i + 1) + " can with sprite added to card, cans count " + canCount);
                    }
                    break;

                case priorityState:
                    activity.addLine("20 cans of sprite delivered to sort station");
                    synchronized (sortStation) {
                        activity.addLine("Sprite line is lock sort station");


                        for (int i = 0; i < 20; i++) {
                            activity.addLine("Sort station is packing... " + (i + 1) + " can of sprite is packing");
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        activity.addLine("Sprite packing is finished");
                    }
                    break;
            }
        }
    }
}
