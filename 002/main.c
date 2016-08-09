#include <stdio.h>

#define ABS(x) (x > 0 ? x : (x) * -1)
#define MAX(x, y) (x > y ? x : y)

typedef enum Bot {
  BLUE,
  ORANGE
} Bot;

void handle_case(int case_number);

int main(int argc, char *argv[]) {
  int case_count;
  scanf("%d ", &case_count);

  for (int i = 0; i < case_count; i++) {
	handle_case(i + 1);
  }

  return 0;
}

void handle_case(int case_number) {
  int button_count;
  scanf("%d ", &button_count);

  // Both bots start at button 1 at time 0
  int bot_position[2] = {1, 1};
  int bot_last_time[2] = {0, 0};

  int last_button_pushed_time = 0;

  for (int i = 0; i < button_count; i++) {
	int button;
	char bot_code;
	scanf("%c %d ", &bot_code, &button);

	Bot bot = bot_code == 'B' ? BLUE : ORANGE;

	// Distance from the bot's last position to the new button's
	// position.
	int distance = ABS(button - bot_position[bot]);

	// Minimum time at which the bot can physically get to the button,
	// calculated by adding the bot's last required position (when it
	// had to be there to push a button) to the distance.
	int bot_time = distance + bot_last_time[bot];

	// If the bot gets to the next button before the last button was
	// pushed, it still has to wait around until the last one is
	// pushed.  And then we add one for the time required to actually
	// push the button.
	last_button_pushed_time = MAX(bot_time, last_button_pushed_time) + 1;

	bot_position[bot] = button;
	bot_last_time[bot] = last_button_pushed_time;
  }

  printf("Case #%d: %d\n", case_number, last_button_pushed_time);
}
