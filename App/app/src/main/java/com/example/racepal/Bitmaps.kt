package com.example.racepal

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color.alpha
import android.graphics.Color.argb
import android.graphics.Color.blue
import android.graphics.Color.green
import android.graphics.Color.red
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.get
import androidx.core.graphics.set
import com.example.racepal.ui.theme.LightBlue
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

/**
 * Applies a blur in a circular area around the center of the bitmap, starting from the specified radius.
 *
 * @param blurLevel Defines the amount of blur, at least 1.
 * @param radius Defines the distance from the bitmap center starting from which the blur is applied.
 * @param transition Defines the width of the blur ring.
 * @param targetColor Optionally defines the ARGB color which the blur should gradually fade into as the perimeter is approached.
 *
 * @return Returns a new bitmap with the specified blur applied.
 */
fun Bitmap.outerBlur(blurLevel: Int, radius: Int, transition: Int, targetColor: Color? = null): Bitmap {
    val centerX = this.width/2
    val centerY = this.height/2
    fun r(i: Int, j: Int): Int = Math.sqrt(((i-centerX)*(i-centerX)+(j-centerY)*(j-centerY)).toDouble()).toInt()

    val target = targetColor?.toArgb() ?: 0
    val targetA = alpha(target)
    val targetR = red(target)
    val targetG = green(target)
    val targetB = blue(target)

    check(this.config == Bitmap.Config.ARGB_8888, {"Invalid bitmap config."})
    check(blurLevel > 0, {"Radius must be positive."})
    val result = this.copy(Bitmap.Config.ARGB_8888, true)
    for (i in 0 .. this.width - 1) {
        for (j in 0 .. this.height - 1) {
            val rad = r(i,j)
            val end = (rad - radius).toDouble()/transition
            if (end > 1.0 || end < 0.0) continue

            var a = 0.0
            var r = 0.0
            var g = 0.0
            var b = 0.0

            val cols = mutableListOf<Int>()
            for (x in -blurLevel .. blurLevel) {
                if (i+x >= 0 && i+x < this.width) {
                    //horizontal ray
                    cols.add(this[i+x,j])
                    //regular diagonal ray
                    if (j+x >= 0 && j+x<this.height) cols.add(this[i+x,j+x])
                    //opposite diagonal ray
                    if (j-x >= 0 && j-x<this.height) cols.add(this[i+x,j-x])
                }
                //vertical ray
                if (j+x >= 0 && j+x<this.width) cols.add(this[i,j+x])
            }

            for (col in cols) {a += alpha(col); r+=red(col); g+=green(col); b+=blue(col);}
            a/=cols.size
            r/=cols.size
            g/=cols.size
            b/=cols.size

            a = a*(1-end)+targetA*end
            r = r*(1-end)+targetR*end
            g = g*(1-end)+targetG*end
            b = b*(1-end)+targetB*end
            result.set(i, j, argb(a.toInt(),r.toInt(),g.toInt(),b.toInt()))
        }
    }
    return result
}

/**
 * Adds an empty transparent margin to the bitmap.
 *
 * @param margin The size of the margin.
 *
 * @return A new bitmap, with the original in the center, and a margin of specified size around.
 */
fun Bitmap.addMargin(margin: Int): Bitmap {
    val result = Bitmap.createBitmap(width + margin*2, height + margin*2, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(result)
    canvas.drawBitmap(this, margin.toFloat(), margin.toFloat(), null)
    return result
}

fun Bitmap.resize(width: Int, height: Int): Bitmap {
    return Bitmap.createScaledBitmap(this, width, height, true)
}

fun Context.getRunnerBitmap(size: Int): Bitmap {
    val iconSize = (size*0.9).toInt()
    val margin = (size*0.05).toInt()
    val blurRadius = (size*0.35).toInt()
    val blurWidth = (size*0.15).toInt()
    val res = BitmapFactory.decodeResource(resources, R.drawable.runner)
    return res.resize(iconSize, iconSize).addMargin(margin).outerBlur(20, blurRadius, blurWidth, LightBlue.copy(alpha = 0.3f))
}

fun Bitmap.toRequestBody(): RequestBody {
    val stream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray().toRequestBody("image/png".toMediaType())
}
fun Bitmap.toMultipartPart(fieldName: String = "image", fileName: String = "image.png"): MultipartBody.Part {
    return MultipartBody.Part.createFormData(fieldName, fileName, this.toRequestBody())
}