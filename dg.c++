#include <Arduino.h>

#define LED_COUNT 5
#define BUTTON_PUSH 32
#define BUTTON_POP 33

int ledPins[LED_COUNT] = {18, 19, 21, 22, 23}; // Pines donde conectar los LEDs

struct Node {
    int ledIndex;
    Node* next;
};

class Stack {
private:
    Node* top;
    int size;
public:
    Stack() { top = nullptr; size = 0; }
    ~Stack() { clear(); }
    
    void push(int ledIndex) {
        if (size >= LED_COUNT) return; // Stack lleno
        Node* newNode = new Node{ledIndex, top};
        top = newNode;
        size++;
        digitalWrite(ledPins[ledIndex], HIGH);
    }
    
    void pop() {
        if (size == 0) return; // Stack vacío
        Node* temp = top;
        top = top->next;
        digitalWrite(ledPins[temp->ledIndex], LOW);
        delete temp;
        size--;
    }
    
    void clear() {
        while (top) pop();
    }
};

Stack ledStack;

void IRAM_ATTR handlePush() {
    for (int i = 0; i < LED_COUNT; i++) {
        bool isOn = digitalRead(ledPins[i]);
        if (!isOn) { ledStack.push(i); break; }
    }
}

void IRAM_ATTR handlePop() {
    ledStack.pop();
}

void setup() {
    for (int i = 0; i < LED_COUNT; i++) {
        pinMode(ledPins[i], OUTPUT);
        digitalWrite(ledPins[i], LOW);
    }
    pinMode(BUTTON_PUSH, INPUT_PULLUP);
    pinMode(BUTTON_POP, INPUT_PULLUP);
    
    attachInterrupt(digitalPinToInterrupt(BUTTON_PUSH), handlePush, FALLING);
    attachInterrupt(digitalPinToInterrupt(BUTTON_POP), handlePop, FALLING);
}

void loop() {
}