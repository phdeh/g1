package src

import javax.swing.JFrame
import javax.swing.plaf.synth.SynthGraphicsUtils.paintIcon
import javax.swing.text.StyleConstants.setForeground
import javax.swing.JLabel
import javax.swing.plaf.synth.SynthGraphicsUtils.getIconHeight
import javax.swing.plaf.synth.SynthGraphicsUtils.getIconWidth
import java.awt.Color.white
import java.awt.image.BufferedImage
import org.scilab.forge.jlatexmath.TeXConstants
import org.scilab.forge.jlatexmath.TeXIcon
import org.scilab.forge.jlatexmath.TeXFormula
import java.awt.*
import java.awt.AWTEventMulticaster.getListeners
import java.awt.Toolkit.getDefaultToolkit
import java.awt.AWTEventMulticaster.getListeners
import kotlin.math.roundToInt


class Window : JFrame() {

    private val bi2 = BufferedImage(1000, 500, BufferedImage.TYPE_4BYTE_ABGR)

    init {
        setSize(1500, 300)
        val dimension = Toolkit.getDefaultToolkit().getScreenSize()
        val x = ((dimension.getWidth() - width) / 2).toInt()
        val y = ((dimension.getHeight() - height) / 2).toInt()
        setLocation(x, y)
        isVisible = true
        extendedState = JFrame.MAXIMIZED_BOTH;
    }

    private var _formula = ""

    var formula
        get() = _formula
        set(value) {
            _formula = value
            repaint()
        }

    private val gr = bi2.graphics
    private var y0 = bi2.height
    private var pr = 0
    private var mx = 0
    private var prv = 0
    private var div = 1000

    fun progress(value: Double) {
        val y = (value / div * bi2.height).roundToInt()
        if (y < y0)
            gr.color = Color.CYAN
        else
            gr.color = Color.ORANGE
        prv += value.roundToInt()
        if (y < bi2.height)
            gr.drawLine(pr, bi2.height - y, pr, bi2.height - y0)
        y0 = y
        pr++
        if (pr == 10) {
            div = prv / 10
        }
    }


    override fun paint(g3: Graphics) {
        val bi = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        val g = bi.graphics
        g.color = Color.white
        g.fillRect(0, 0, width, height)
        val formula = TeXFormula(_formula)

        // render the formla to an icon of the same size as the formula.
        val icon = formula
            .createTeXIcon(TeXConstants.STYLE_DISPLAY, 20f)

        // insert a border
        icon.setInsets(Insets(5, 5, 5, 5))

        // now create an actual image of the rendered equation
        val image = BufferedImage(
            icon.getIconWidth(),
            icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB
        )
        val g2 = image.createGraphics()
        g2.color = Color(0, 0, 0, 0)
        g2.fillRect(0, 0, icon.getIconWidth(), icon.getIconHeight())
        val jl = JLabel()
        jl.foreground = Color(0, 0, 0)
        icon.paintIcon(jl, g2, 0, 0)
        // at this point the image is created, you could also save it with ImageIO

        // now draw it to the screen
        g.drawImage(bi2, 0, 0, width, height, null)
        g.drawImage(image, width / 2 - image.width / 2, height / 2 - image.height / 2, null)
        g3.drawImage(bi, 0, 0, null)
    }
}