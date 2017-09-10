package com.binbo_kosakudan.youtuber

import java.io.{BufferedReader, InputStreamReader}

import org.scalatra._

import scala.sys.process._
import java.util.Date

class MyScalatraServlet extends YoutuberStack
  with FlashMapSupport {

  val dlCmd = "/usr/bin/youtube-dl"
  val dlOptions = "-f mp4"
  val cnvCmd = "/usr/bin/ffmpeg"

  get("/") {
    contentType="text/html"
    jade("/index",
      "flash" -> flash,
      "files" -> new java.io.File("./")
        .listFiles.filter(f => f.getName.endsWith(".mp4") || f.getName.endsWith(".mp3"))
//        .map(f => f.getName)
        .sortWith((l, r) => l.getName > r.getName)
        .toList)
  }

  get("/:name") {
    contentType="application/octet-stream"
    val file = new java.io.File(params("name"))
    file
  }

  post("/") {
    val filename = "%tY%<tm%<td%<tH%<tM%<tS" format new Date

    {
      val cmd = dlCmd + " " + dlOptions + " " + params("youtube-url") + " -o " + filename + ".mp4"
      println(cmd)
      val r = Process(cmd) !!

      println(r)
    }
    {
      val cmd = cnvCmd + " -i " + filename + ".mp4" + " -acodec libmp3lame -ab 128k " + filename + ".mp3"
      println(cmd)
      val r = Process(cmd) !!

      println(r)
    }
    flash("notice") = "ダウンロード完了しました"

    redirect("/")
  }
}
