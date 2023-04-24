package com.example.ticktactoe.customView

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.ticktactoe.R


class TicTacToeView : View, ValueAnimator.AnimatorUpdateListener {

    // Constructor
    constructor(ctx: Context) : super(ctx)
    // Constructor with attributes
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)
    // paint object declaration
    private val paint = Paint()
    private val animatePaint = Paint()
    private val textPaint = Paint()
    private val highLightPaint = Paint()
    private val path = Path()
    private lateinit var squares: Array<Array<Rect>>
    private lateinit var squareData: Array<Array<String>>
    var squarePressListener: SquarePressedListener? = null
    // moveX == X for the X move
    // moveY == Y for the O move
    val moveX = "X"
    val moveY = "O"
    // measurement temporary variables for calculation
    val COUNT = 3
    val X_PARTITION_RATIO = 1 / 3f
    val Y_PARTITION_RATIO = 1 / 3f
    var rectIndex = Pair(0, 0)
    var touching: Boolean = false
    var winCoordinates: Array<Int> = Array(4, { -1 })
    var shouldAnimate = false


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size = Math.min(measuredHeight, measuredWidth)
        setMeasuredDimension(size, size)
    }
    // Calling the init() function
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        init()
    }

    // init for the paint obejct setup
    @SuppressLint("ResourceAsColor")
    private fun init() {
        paint.color = ContextCompat.getColor(context, R.color.colorPrimary)
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = resources.displayMetrics.density * 5

        animatePaint.color = paint.color
        animatePaint.isAntiAlias = paint.isAntiAlias
        animatePaint.style = paint.style
        animatePaint.strokeWidth = paint.strokeWidth

        //textPaint.color = R.color.red
        textPaint.isAntiAlias = true
        textPaint.textSize = resources.displayMetrics.scaledDensity * 70

        highLightPaint.color = ContextCompat.getColor(context, R.color.black)
        highLightPaint.style = Paint.Style.FILL
        highLightPaint.isAntiAlias = true
        initializeTicTacToeSquares()
    }
    // LOGIC TO INTIALIZE THE GRIDS SQUARE
    private fun initializeTicTacToeSquares() {
        squares = Array(3) { Array(3) { Rect() } }
        squareData = Array(3) { Array(3) { "" } }

        val xUnit = (width * X_PARTITION_RATIO).toInt() // one unit on x-axis
        val yUnit = (height * Y_PARTITION_RATIO).toInt() // one unit on y-axis

        for (j in 0 until COUNT) {
            for (i in 0 until COUNT) {
                squares[i][j] = Rect(i * xUnit, j * yUnit, (i + 1) * xUnit, (j + 1) * yUnit)
            }

        }

    }
    // ONTOUCH EVENTS LOGIC
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) {
            return false
        }
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                rectIndex = getRectIndexesFor(x, y)
                touching = true
                invalidate(squares[rectIndex.first][rectIndex.second])
            }
            MotionEvent.ACTION_MOVE -> {

            }
            MotionEvent.ACTION_UP -> {
                touching = false
                invalidate(squares[rectIndex.first][rectIndex.second])
                val (finalX1, finalY1) = getRectIndexesFor(x, y)
                if ((finalX1 == rectIndex.first) && (finalY1 == rectIndex.second)) { // if initial touch and final touch is in same rectangle or not
                    squarePressListener?.onSquarePressed(rectIndex.first, rectIndex.second)
                }

            }
            MotionEvent.ACTION_CANCEL -> {
                touching = false
            }

        }
        return true
    }
    // GET RECTANGLE INDEX FOR FINDING THE winCordinates method will return pair<int,int>
    fun getRectIndexesFor(x: Float, y: Float): Pair<Int, Int> {
        squares.forEachIndexed { i, rects ->
            for ((j, rect) in rects.withIndex()) {
                if (rect.contains(x.toInt(), y.toInt()))
                    return Pair(i, j)
            }
        }
        return Pair(-1, -1) // x, y do not lie in our view
    }
    // ONDRAW FUNCTION
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawVerticalLines(canvas)
        drawHorizontalLines(canvas)
        drawSquareStates(canvas)
        if (shouldAnimate) {
            canvas.drawPath(path, animatePaint)
        }
        if (touching) {
            drawHighlightRectangle(canvas)
        }
    }
    // WINNING LINE ANIMATION WHICH HAVE DURATION OF 600MS
    private fun animateWin() {
        val valueAnimator = ValueAnimator.ofFloat(1f, 0f)
        valueAnimator.duration = 600
        valueAnimator.addUpdateListener(this)
        valueAnimator.start()
    }

    override fun onAnimationUpdate(animation: ValueAnimator) {
        val measure = PathMeasure(path, false)
        val phase = (measure.length * (animation.animatedValue as Float))
        animatePaint.pathEffect = createPathEffect(measure.length, phase)
        invalidate()
    }

    private fun createPathEffect(pathLength: Float, phase: Float): PathEffect {
        return DashPathEffect(floatArrayOf(pathLength, pathLength),
                phase)
    }
    // DRAWING SQUARESTATES ON CANVAS
    private fun drawSquareStates(canvas: Canvas) {
        for ((i, textArray) in squareData.withIndex()) {
            for ((j, text) in textArray.withIndex()) {
                if (text.isNotEmpty()) {
                    drawTextInsideRectangle(canvas, squares[i][j], text)
                }
            }
        }
    }
    // DRAWHIGHLIGHTED RECTANGLE WHENVER USER TOUCH THE PARTICULAR RECTANGLE OF GRID
    private fun drawHighlightRectangle(canvas: Canvas) {
        canvas.drawRect(squares[rectIndex.first][rectIndex.second], highLightPaint)
    }
    // DRAWING TEXT INSIDE A GRID RECTANGLE IF THE str == "X" means player - X then textPaint colour will be red
    // If str == "O" then textPaint color will be Black fetched from the color.xml in resource folder.
    @SuppressLint("ResourceAsColor")
    private fun drawTextInsideRectangle(canvas: Canvas, rect: Rect, str: String) {
        val xOffset = textPaint.measureText(str) * 0.5f
        val yOffset = textPaint.fontMetrics.ascent * -0.4f
        val textX = (rect.exactCenterX()) - xOffset
        val textY = (rect.exactCenterY()) + yOffset
        if(str == "X")
        {
            textPaint.color = ContextCompat.getColor(context, R.color.red)
        }
        else
        {
            textPaint.color=ContextCompat.getColor(context, R.color.black)
        }
        canvas.drawText(str, textX, textY, textPaint)
    }
    // DrawVerctical lines for making of Grid
    private fun drawVerticalLines(canvas: Canvas) {
        canvas.drawLine(width * X_PARTITION_RATIO, 0f, width * X_PARTITION_RATIO, height.toFloat(), paint)
        canvas.drawLine(width * (2 * X_PARTITION_RATIO), 0f, width * (2 * X_PARTITION_RATIO), height.toFloat(), paint)
    }
    // DrawHorizontal lines for making of Grid
    private fun drawHorizontalLines(canvas: Canvas) {
        canvas.drawLine(0f, height * Y_PARTITION_RATIO, width.toFloat(), height * Y_PARTITION_RATIO, paint)
        canvas.drawLine(0f, height * (2 * Y_PARTITION_RATIO), width.toFloat(), height * (2 * Y_PARTITION_RATIO), paint)
    }

    interface SquarePressedListener {
        fun onSquarePressed(i: Int, j: Int)
    }
    // DRAW X AT GIVEN POSITION ON SQUARE OF GRID
    fun drawXAtPosition(x: Int, y: Int) {
        squareData[x][y] = moveX
        invalidate(squares[x][y])
    }
    // DRAW O AT GIVEN POSITION ON SQUARE OF GRID
    fun drawOAtPosition(x: Int, y: Int) {
        squareData[x][y] = moveY
        invalidate(squares[x][y])
    }
    // LOGIC FOR CLEAR/RESET
    fun reset() {
        squareData = Array(3) { Array(3) { "" } }
        winCoordinates = Array(4) { -1 }
        path.reset()
        shouldAnimate = false
        invalidate()
    }
    // DRAWING THE WINNING LINE USING WINNING COR-ORDINATES.
    fun animateWin(x1: Int, y1: Int, x3: Int, y3: Int) {
        winCoordinates = arrayOf(x1, y1, x3, y3)
        if (winCoordinates[0] < 0) return
        val centerX = squares[winCoordinates[0]][winCoordinates[1]].exactCenterX()
        val centerY = squares[winCoordinates[0]][winCoordinates[1]].exactCenterY()
        val centerX2 = squares[winCoordinates[2]][winCoordinates[3]].exactCenterX()
        val centerY2 = squares[winCoordinates[2]][winCoordinates[3]].exactCenterY()

        path.reset()
        path.moveTo(centerX, centerY)
        path.lineTo(centerX2, centerY2)
        shouldAnimate = true
        animateWin()
    }

}