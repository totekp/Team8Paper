package models

case class Paper(
id: String,
title: String,
tags: List[String]
                  )

/*
kind -> video, image, text,
 */
case class Element(
  id: String,
  paperid: String,
  kind: String,
  data: String,
  x: Int,
  y: Int,
  z: Int
                    )
