package lib

// ButtonPress defines a required button press for a given robot color
type ButtonPress struct {
	Color  rune
	Button int
}

const (
	// ORANGE is a valid robot color
	ORANGE = 'O'
	// BLUE is a valid robot color
	BLUE = 'B'
)

func peekNextPress(b []ButtonPress) map[rune]int {
	result := map[rune]int{}
	for _, bp := range b {
		if _, ok := result[bp.Color]; !ok {
			result[bp.Color] = bp.Button
		}
		if len(result) == 2 {
			break
		}
	}
	return result
}
