import frankel.uriel.vizai.R

/*
Copyright (c) 2019 Kotlin Data Classes Generated from JSON powered by http://www.json2kotlin.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

For support, please feel free to contact me at https://www.linkedin.com/in/syedabsar */


class Emotion {

    var anger: Double = 0.0
    var contempt: Double = 0.0
    var disgust: Double = 0.0
    var fear: Double = 0.0
    var happiness: Double = 0.0
    var neutral: Double = 0.0
    var sadness: Double = 0.0
    var surprise: Double = 0.0

    fun getEmotion(): Int? {
        return listOf((anger to R.string.anger), (contempt to R.string.contempt),
            (disgust to R.string.disgust),(fear to R.string.fear),(happiness to R.string.happiness),
            (neutral to R.string.neutral),(sadness to R.string.sadness),(surprise to R.string.surprise)).maxBy { emo -> emo.first}?.second
    }

}