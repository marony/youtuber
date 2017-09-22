package com.binbo_kosakudan.youtuber

import java.io.{BufferedReader, InputStreamReader}

import org.scalatra._

import scala.sys.process._
import java.util.Date

class MyScalatraServlet extends YoutuberStack
  with FlashMapSupport {

  val getTitleCmd = "/usr/bin/youtube-dl -e"
  val dlCmd = "/usr/bin/youtube-dl"
  val dlOptions = "-f mp4"
  val cnvMP4Cmd = "/usr/bin/ffmpeg"
  val cnvMP3Cmd = "/usr/bin/ffmpeg"

  get("/") {
    contentType="text/html"
    jade("/index",
      "flash" -> flash,
      "files" -> new java.io.File("./")
        .listFiles.filter(f => f.getName.endsWith(".mp4") || f.getName.endsWith(".mp3"))
//        .map(f => f.getName)
        .sortWith((l, r) => l.lastModified > r.lastModified)
        .toList)
  }

  get("/:name") {
    contentType="application/octet-stream"
    val file = new java.io.File(params("name"))
    file
  }

  post("/") {
    val tmpFilename = "%tY%<tm%<td%<tH%<tM%<tS" format new Date

    // タイトルを取得
    val title = {
      val cmd = getTitleCmd + " " + params("youtube-url")
      println(cmd)
      val r = Process(cmd) !!

      println(r)
      r.trim
    }
    // MP4ダウンロード
    {
      val cmd = dlCmd + " " + dlOptions + " " + params("youtube-url") + " -o " + tmpFilename + ".mp4"
      println(cmd)
      val r = Process(cmd) !!

      println(r)
    }
    // MP4の音量を変換
    {
      val cmd = cnvMP4Cmd + " -i " + tmpFilename + ".mp4" + " -y -af dynaudnorm " + title + ".mp4"
      println(cmd)
      val r = Process(cmd) !!

      println(r)
    }
    // MP3に変換
    {
      val cmd = cnvMP3Cmd + " -i " + tmpFilename + ".mp4" + " -y -af dynaudnorm -acodec libmp3lame -ab 128k " + title + ".mp3"
      println(cmd)
      val r = Process(cmd) !!

      println(r)
    }
    // テンポラリファイルの削除
    try {

    }
    catch {
      case e: Exception =>
        println(e)
    }
    flash("notice") = "ダウンロード完了しました"

    redirect("/")
  }
}
