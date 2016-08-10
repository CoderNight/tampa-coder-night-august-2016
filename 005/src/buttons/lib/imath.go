package lib

// yay for go not supporting ints in math.Abs
func abs(i int) int {
	if i < 0 {
		return -i
	}
	return i
}
