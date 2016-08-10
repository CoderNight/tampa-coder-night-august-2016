package lib

// State stores the position of each robot by color and overal elapsed time
type State struct {
	Position map[rune]int
	Time     int
}

var (
	// STARTING describes a default starting state
	STARTING = State{
		map[rune]int{ORANGE: 1, BLUE: 1},
		0,
	}
)

// Solve returns the number of seconds before a ButtonPress sequence will finish
func Solve(b []ButtonPress, s State) int {
	next := peekNextPress(b)
	time := abs(next[b[0].Color]-s.Position[b[0].Color]) + 1
	s = moveTo(s, time, next)
	if len(b) == 1 {
		return s.Time
	}
	return Solve(b[1:], s)
}

func moveTo(s State, time int, next map[rune]int) (result State) {
	result.Position = map[rune]int{
		ORANGE: updatePosition(s.Position[ORANGE], time, next[ORANGE]),
		BLUE:   updatePosition(s.Position[BLUE], time, next[BLUE]),
	}
	result.Time = s.Time + time // add 1 for button press
	return
}

func updatePosition(position, time, goal int) int {
	if position == goal {
		return position
	}
	if abs(position-goal) <= time {
		return goal
	}
	if position < goal {
		return position + time
	}
	return position - time
}
