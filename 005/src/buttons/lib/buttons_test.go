package lib

import "testing"

type buttonPressTest struct {
	buttons  []ButtonPress
	expected int
}

func TestButtonsSimple(t *testing.T) {
	examples := []buttonPressTest{
		{[]ButtonPress{{ORANGE, 1}}, 1},
		{[]ButtonPress{{BLUE, 1}, {ORANGE, 1}}, 2},
	}
	for _, example := range examples {
		result := Solve(example.buttons, STARTING)
		if result != example.expected {
			t.Error("For", example, "wanted", example.expected, "got", result)
		}
	}
}

func TestButtonsMovingForward(t *testing.T) {
	examples := []buttonPressTest{
		{[]ButtonPress{{ORANGE, 3}}, 3},
		{[]ButtonPress{{BLUE, 5}, {ORANGE, 8}}, 8},
	}
	for _, example := range examples {
		result := Solve(example.buttons, STARTING)
		if result != example.expected {
			t.Error("For", example, "wanted", example.expected, "got", result)
		}
	}
}

func TestButtonsTricky(t *testing.T) {
	examples := []buttonPressTest{
		{[]ButtonPress{{BLUE, 2}, {BLUE, 1}}, 4},
		{[]ButtonPress{{ORANGE, 3}, {ORANGE, 1}, {ORANGE, 20}}, 26},
		{[]ButtonPress{{ORANGE, 2}, {BLUE, 1}, {BLUE, 2}, {ORANGE, 4}}, 6},
		{[]ButtonPress{{ORANGE, 5}, {ORANGE, 8}, {BLUE, 100}}, 100},
	}
	for _, example := range examples {
		result := Solve(example.buttons, STARTING)
		if result != example.expected {
			t.Error("For", example, "wanted", example.expected, "got", result)
		}
	}
}
