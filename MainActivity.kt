package com.example.nokiaemulator

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        setContentView(NokiaView(this))
    }
}

class NokiaView(context: Context) : View(context) {
    private val items = arrayOf("Messages","Contacts","Log","Settings","Gallery","Media","PTT","Organizer","Apps.")
    private val symbols = arrayOf("✉","☎","↕","🔧","♫","▣","▤","▦","▦")
    private var selected = 0
    private var page = "Menu"
    private var clock = ""
    private val p = Paint(Paint.ANTI_ALIAS_FLAG)
    private val white = Color.WHITE
    private val navy = Color.rgb(5, 20, 105)

    override fun onDraw(c: Canvas) {
        super.onDraw(c)
        val w = width.toFloat(); val h = height.toFloat()
        c.drawColor(Color.rgb(215,215,215))
        val screenH = h * .60f
        val screen = RectF(w*.055f, 0f, w*.945f, screenH)
        val bg = LinearGradient(0f,0f,0f,screenH, intArrayOf(0xff9bd5e8.toInt(),0xff07105e.toInt(),0xff07549a.toInt(),0xff19a5a7.toInt(),0xffe8ffff.toInt()), null, Shader.TileMode.CLAMP)
        p.shader=bg; c.drawRect(screen,p); p.shader=null

        // Soft oval shapes from the original S40 wallpaper
        p.color=0x333d9ad0
        val ovals = arrayOf(
            RectF(w*.15f,screenH*.28f,w*.58f,screenH*.39f),
            RectF(w*.56f,screenH*.35f,w*.91f,screenH*.45f),
            RectF(w*.08f,screenH*.55f,w*.62f,screenH*.70f),
            RectF(w*.18f,screenH*.68f,w*.86f,screenH*.92f),
            RectF(w*.55f,screenH*.45f,w*.97f,screenH*.59f))
        ovals.forEach { c.drawOval(it,p) }; p.color=white

        // Status bar
        p.textSize=w*.055f; p.typeface=Typeface.DEFAULT_BOLD
        c.drawText("▥  ▂▅▇",w*.07f,screenH*.055f,p)
        p.color=0xff39d84c.toInt(); c.drawRoundRect(w*.25f,screenH*.012f,w*.37f,screenH*.047f,8f,8f,p)
        p.color=0xfff5d329.toInt(); c.drawText("✉",w*.40f,screenH*.055f,p)
        p.color=white; p.textSize=w*.047f
        clock=SimpleDateFormat("HH:mm",Locale.getDefault()).format(Date())
        c.drawText(clock,w*.80f,screenH*.055f,p)
        p.typeface=Typeface.create("sans-serif-light",Typeface.NORMAL)
        p.textSize=w*.075f
        c.drawText(page,w*.06f,screenH*.115f,p)

        if(page=="Menu") drawMenu(c,w,screenH) else drawSubPage(c,w,screenH)

        // Softkey strip
        p.shader=LinearGradient(0f,screenH*.94f,0f,screenH,0xffb9eeee.toInt(),white,Shader.TileMode.CLAMP)
        c.drawRect(0f,screenH*.94f,w,screenH,p); p.shader=null
        p.color=navy; p.textSize=w*.060f; p.typeface=Typeface.DEFAULT
        c.drawText("Options",w*.02f,screenH*.987f,p)
        p.typeface=Typeface.DEFAULT_BOLD
        val center=if(page=="Menu") "Select" else "OK"
        c.drawText(center,w*.39f,screenH*.987f,p)
        p.typeface=Typeface.DEFAULT
        c.drawText("Exit",w*.84f,screenH*.987f,p)

        drawKeypad(c,w,h,screenH)
    }

    private fun drawMenu(c:Canvas,w:Float,sh:Float) {
        val startY=sh*.20f
        val cellW=w*.29f; val cellH=sh*.235f
        for(i in 0..8) {
            val col=i%3; val row=i/3
            val x=w*.04f+col*w*.30f
            val y=startY+row*cellH
            if(i==selected) {
                p.shader=LinearGradient(x,y,x,y+cellH,intArrayOf(0xffe4ddf5.toInt(),0x445d8ed2),null,Shader.TileMode.CLAMP)
                c.drawRoundRect(x,y,x+cellW,y+cellH*.94f,18f,18f,p); p.shader=null
            }
            p.color=0xffffcf23.toInt()
            p.textSize=w*.105f; p.typeface=Typeface.DEFAULT_BOLD
            val icon=when(i){0->"✉";1->"▣";2->"↕";3->"🔧";4->"♫";5->"▤";6->"▥";7->"▦";else->"▦"}
            c.drawText(icon,x+cellW*.32f,y+cellH*.55f,p)
            p.color=0xffe9eafa.toInt(); p.textSize=w*.048f; p.typeface=Typeface.create("sans-serif-light",0)
            val label=items[i]
            val tw=p.measureText(label)
            c.drawText(label,x+(cellW-tw)/2,y+cellH*.82f,p)
        }
        p.color=0xffdceaff.toInt()
        c.drawRoundRect(w*.928f,sh*.16f,w*.945f,sh*.91f,6f,6f,p)
        p.color=white
        c.drawRoundRect(w*.925f,sh*.16f,w*.945f,sh*.34f,6f,6f,p)
    }

    private fun drawSubPage(c:Canvas,w:Float,sh:Float) {
        p.color=white; p.textSize=w*.055f
        val lines=when(page) {
            "Messages"->"New message\nInbox\nSent items\nDrafts"
            "Contacts"->"Add contact\nNames\nSearch"
            "Settings"->"Profiles\nDisplay\nTones\nPhone settings"
            "Gallery"->"Images\nVideo clips\nThemes"
            "Media"->"Music player\nRadio\nRecorder"
            "Organizer"->"Calendar\nTo-do list\nNotes\nAlarm clock"
            else->"Open\nOptions"
        }
        var y=sh*.24f
        lines.split("\\n").forEach { c.drawText(it,w*.10f,y,p); y+=sh*.09f }
    }

    private fun drawKeypad(c:Canvas,w:Float,h:Float,screenH:Float) {
        val top=screenH+h*.035f
        val bottom=h*.985f
        val gap=w*.006f
        val leftW=w*.48f
        val rightX=w*.52f
        val rowH=(bottom-top)/4f
        val nums=arrayOf(arrayOf("1","2","3"),arrayOf("4","5","6"),arrayOf("7","8","9"),arrayOf("*","0","#"))
        for(r in 0..3) for(col in 0..2) {
            val x=col*leftW/3f
            val rect=RectF(x+gap,top+r*rowH+gap,x+leftW/3f-gap,top+(r+1)*rowH-gap)
            key(c,rect,nums[r][col])
        }
        val keys=arrayOf(arrayOf("L","","R"),arrayOf("↖","↑","↗"),arrayOf("←","F","→"),arrayOf("↙","↓","↘"))
        for(r in 0..3) for(col in 0..2) {
            if(keys[r][col].isNotEmpty()) {
                val x=rightX+col*(w-rightX)/3f
                val rect=RectF(x+gap,top+r*rowH+gap,x+(w-rightX)/3f-gap,top+(r+1)*rowH-gap)
                key(c,rect,keys[r][col])
            }
        }
    }

    private fun key(c:Canvas,r:RectF,s:String) {
        p.color=0xffeeeeee.toInt(); p.style=Paint.Style.STROKE; p.strokeWidth=1.5f
        c.drawRoundRect(r,20f,20f,p); p.style=Paint.Style.FILL
        p.color=0xff11117a.toInt(); p.textSize=width*.045f; p.typeface=Typeface.DEFAULT
        val tw=p.measureText(s)
        c.drawText(s,r.centerX()-tw/2,r.centerY()+p.textSize*.35f,p)
    }

    override fun onTouchEvent(e:MotionEvent):Boolean {
        if(e.action!=MotionEvent.ACTION_UP) return true
        val w=width.toFloat(); val sh=height*.60f
        val x=e.x; val y=e.y
        if(y<sh*.94f && page=="Menu") {
            if(y>sh*.18f && y<sh*.91f) {
                val col=((x-w*.04f)/(w*.30f)).toInt().coerceIn(0,2)
                val row=((y-sh*.20f)/(sh*.235f)).toInt().coerceIn(0,2)
                selected=row*3+col
            }
        } else if(y<sh && page!="Menu") { page="Menu" }
        else {
            val top=sh+height*.035f
            val right=x>w*.52f
            if(right) {
                val row=((y-top)/((height*.985f-top)/4f)).toInt().coerceIn(0,3)
                val col=(((x-w*.52f)/(w*.48f/3f)).toInt()).coerceIn(0,2)
                when {
                    row==1 && col==1 -> selected=(selected-3+9)%9
                    row==2 && col==0 -> selected=if(selected%3==0) selected else selected-1
                    row==2 && col==2 -> selected=if(selected%3==2) selected else selected+1
                    row==3 && col==1 -> selected=(selected+3)%9
                    row==0 && col==0 -> page="Menu"
                    row==0 && col==2 -> page="Menu"
                    row==2 && col==1 -> openSelected()
                }
            } else {
                val row=((y-top)/((height*.985f-top)/4f)).toInt().coerceIn(0,3)
                val col=((x/(w*.48f/3f)).toInt()).coerceIn(0,2)
                if(row==0 && col==0) page="Menu"
                if(row==3 && col==1) page="Menu"
            }
        }
        invalidate(); return true
    }

    private fun openSelected() { page=items[selected].removeSuffix(".") }
}
