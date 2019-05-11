package com.agladkov.thread_safe;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.agladkov.thread_safe.models.Kitchen;

public class ObjectActivity extends AppCompatActivity {

    EditText textConsole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object);

        textConsole = findViewById(R.id.textConsole);
        Button btnAdd = findViewById(R.id.addOrder);
        final Kitchen kitchen = new Kitchen(new Kitchen.KitchenLog() {
            @Override
            public void addMessage(final String text) {
                textConsole.post(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        textConsole.setText(textConsole.getText() + text + "\n");
                    }
                });
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Consumer consumer = new Consumer(kitchen);
                consumer.start();

                Cook cook = new Cook(kitchen);
                cook.start();
            }
        });
    }

    static class Cook extends Thread {
        private final Kitchen kitchen;

        Cook(Kitchen kitchen) {
            this.kitchen = kitchen;
        }

        @Override
        public void run() {
            for (int i = 1; i < 6; i++) {
                kitchen.getOrder();
            }
        }
    }

    static class Consumer extends Thread {
        private final Kitchen kitchen;

        Consumer(Kitchen kitchen) {
            this.kitchen = kitchen;
        }

        @Override
        public void run() {
            for (int i = 1; i < 6; i++) {
                kitchen.addOrder();
            }
        }
    }
}
