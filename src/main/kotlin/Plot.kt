import java.awt.Color
import java.awt.geom.Point2D
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.roundToInt

data class PlotConfig(
    val width: Int = 1000,
    val height: Int = 1000,
    val horizontalPadding: Int = 50,
    val verticalPadding: Int = 50,
    val upsideDown: Boolean = true
)

fun List<Dot>.plotTo(path: String, config: PlotConfig = PlotConfig()) {
    val bi = BufferedImage(config.width, config.height, BufferedImage.TYPE_3BYTE_BGR)
    val minX = min(this) { it.x }
    val minY = min(this) { it.y }
    val maxX = max(this) { it.x }
    val maxY = max(this) { it.y }
    val addY = if (config.upsideDown) config.height else 0
    val mulY = if (config.upsideDown) -1 else 1
    bi.graphics.color = Color.WHITE
    toCoilPairs().forEach {
        val x1 =
            ((it.first.x - minX) / (maxX - minX) * (config.width - config.horizontalPadding * 2) + config.horizontalPadding).roundToInt()
        val y1 =
            addY + mulY * ((it.first.y - minY) / (maxY - minY) * (config.height - config.verticalPadding * 2) + config.verticalPadding).roundToInt()
        val x2 =
            ((it.second.x - minX) / (maxX - minX) * (config.width - config.horizontalPadding * 2) + config.horizontalPadding).roundToInt()
        val y2 =
            addY + mulY * ((it.second.y - minY) / (maxY - minY) * (config.height - config.verticalPadding * 2) + config.verticalPadding).roundToInt()
        bi.graphics.drawLine(
            x1, y1, x2, y2
        )
    }
    val file = File(path)
    ImageIO.write(bi, file.extension.toUpperCase(), file)
}