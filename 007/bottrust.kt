package bottrust

import bottrust.Action.*
import bottrust.Hall.Blue
import bottrust.Hall.Orange
import okio.Okio
import rx.Observable
import rx.subjects.BehaviorSubject
import java.util.*

val classLoader = ClassLoader.getSystemClassLoader()

enum class Hall { Orange, Blue }
enum class Action { Move, Stay, PushButton }

data class Case(val sequences: List<CaseSequence>)
data class CaseSequence(val hall: Hall, val position: Int)
data class CaseSequenceMove(val robot: Robot, val action: Action)
data class Robot(val hall: Hall, val position: Int, val sequences: List<CaseSequence>)

fun inputObs(path: String): Observable<String> {
    val source = Okio.buffer(Okio.source(classLoader.getResourceAsStream(path)))
    return Observable.just(source.readUtf8())
}

fun caseFromInputLine(line: String): Case {
    val sequences = ArrayList<CaseSequence>();

    val groupings = line.split(" ")
    val values = groupings.subList(1, groupings.size)

    val iterator = values.iterator()
    while (iterator.hasNext()) {
        val hall = when (iterator.next()) {
            "O" -> Orange
            "B" -> Blue
            else -> throw RuntimeException("Unknown Hall Color")

        }
        val position = iterator.next()

        sequences.add(CaseSequence(hall, position.toInt()))
    }

    return Case(sequences)
}

class CaseEvaluator(val case: Case) {
    fun moves(): HashMap<Hall, List<CaseSequenceMove>> {
        // state
        val currentSeqPos = BehaviorSubject.create<Int>(0)
        val buttonPresses = BehaviorSubject.create<Int>(0)
        val tick = BehaviorSubject.create<Int>(1)

        val orange = BehaviorSubject.create<Robot>(Robot(Orange, 1,
                case.sequences.filter { it.hall == Orange }))
        val blue = BehaviorSubject.create<Robot>(Robot(Blue, 1,
                case.sequences.filter { it.hall == Blue }))

        val initialMovesMap = HashMap<Hall, List<CaseSequenceMove>>()
        initialMovesMap.put(Orange, emptyList())
        initialMovesMap.put(Blue, emptyList())

        val moves = BehaviorSubject.create<HashMap<Hall, List<CaseSequenceMove>>>(initialMovesMap)

        moves.skip(1) // the moves are initially empty
            .doOnNext {
                // each time we move, update the robot state based on the last move
                orange.onNext(it.get(Orange)!!.last().robot)
                blue.onNext(it.get(Blue)!!.last().robot)
            }
            .filter {
                // listen for moves that resulted in a button being pressed
                it.get(Orange)!!.last().action == PushButton
                    || it.get(Blue)!!.last().action == PushButton
            }.subscribe {
                // emit a button press if a move was emitted with PushButton Action
                buttonPresses.onNext(buttonPresses.value + 1)
            }

        // any time a button is pressed, move on to the next button press sequence
        buttonPresses.subscribe { currentSeqPos.onNext(it) }


        // as long as we have a valid current sequence listen to ticks
        return tick.takeWhile { currentSeqPos.value < case.sequences.size }
            .withLatestFrom(moves, orange, blue, currentSeqPos,
                    {tick, moves, orangeRobot, blueRobot, currentSeqPos ->

                val currentSeq = case.sequences.get(currentSeqPos)

                // update the moves Map, adding the next move for each Hall
                moves.put(Orange, moves.get(Orange)!!.plus(nextMoveFor(orangeRobot, currentSeq)))
                moves.put(Blue, moves.get(Blue)!!.plus(nextMoveFor(blueRobot, currentSeq)))

                moves
            }).doOnNext {
                moves.onNext(it) // update the state with the new Map of moves
                tick.onNext(tick.value + 1) // emit next tick
            }.toBlocking().last() // even though we're working with streams, block until we're finished
    }

    private fun nextMoveFor(robot: Robot, currentSeq: CaseSequence): CaseSequenceMove {
        val newRobot: Robot
        val action: Action

        if (robot.sequences.isEmpty()) {
            // if we've consumed all of this Hall's button press sequences - Stay
            return CaseSequenceMove(robot.copy(), Stay)
        }

        // get the Hall's next queued up button press sequence
        val currentRobotSeq = robot.sequences.get(0)

        if (robot.position != currentRobotSeq.position) {
            // if the robot isn't in the right position, we'll need to move to the right spot - Move
            val nextRobotPosition = if (robot.position < currentRobotSeq.position) {
                robot.position + 1
            } else {
                robot.position - 1
            }
            action = Move
            newRobot = robot.copy(position = nextRobotPosition)
        } else if(robot.hall == currentSeq.hall && robot.position == currentSeq.position) {
            // the robot is in the right spot - PushButton
            action = PushButton
            newRobot = robot.copy(sequences = robot.sequences.drop(1)) // consume the current push button sequence
        } else {
            action = Stay
            newRobot = robot
        }

        return CaseSequenceMove(newRobot, action)
    }
}

fun main(args: Array<String>) {
    val run = inputObs("A-large-practice.in")
        .map(String::lines) // map the input string to an array of lines
        .flatMap { Observable.from(it) } // flatten the array into individual lines
        .skip(1) // skip first line, which is a count of how many cases there are
        .filter { !it.isEmpty() } // skip empty lines in the input file
        .map(::caseFromInputLine) // convert each line to a Case object
        .map { CaseEvaluator(it).moves() } // convert each Case object to its set of moves

    var counter = 0
    run.doOnNext { counter += 1 } // count each emission (I'm sure there's a better, more "Rx" way to do this)
        .map { "Case #${counter}: ${it.get(Orange)!!.size}" } // map each case to how many turns it took
        .subscribe(::println) // print each Case's result
}