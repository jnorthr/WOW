package org.jnorthr.wow;
public class WOW
{
    // bean to hold row data
    class Dta
    {
        String title = "";
        String alt = "";
        String href = "";
        String imgsrc = "";
        int imagenumber = 0;
    
//         <a href="#" title="Lake"><span><img src="data1/tooltips/lake.jpg" alt="Lake"/>1</span></a>    
        String toBullet() {
"""         <a href="${href}" title="${title}"><span><img src="tooltips/${imgsrc}" alt="${alt}"/>${imagenumber+1}</span></a>    """
        }

        String toString() {
"""        <li><img src="images/${imgsrc}" alt="${alt}" title="${title}" id="wows1_${imagenumber}"/></li>""".toString()
         } // end of toString()    
    }// end of class


    // current directory this class is executing within - output file is written here
    def cp= "."
    
    // does file already exists ? If so, remove it
    boolean yn = false;
    
    // do we generate a complete HTML file ? 
    boolean addWrapper = true;

    // do we generate printlines ? 
    boolean audit = false;

    // temporary file handle
    File fo;
    
    // list holding rows of beans
    def dta=[];
    
    // temp internal counter to provide images ID tie-breaker plus bullet number
    int imagenumber = 0;
    
    // holds string of HTML text per LI for each image 
    StringBuffer sb = ''<<'' ;
    
    // holds string of HTML text per LI for each bullet-point 
    StringBuffer bullets = ''<<'' ;

    // default constructor
    public WOW()
    {
        buildTemplates();
    } // end of constructor

    
    // non-default constructor to only do WOW in one go
    public WOW(String name)
    {
        buildTemplates();
        setup(name);
        write(head.toString()+"\n");
        write(body.toString()+"\n");
    } // end of constructor
    
    
    // non-default constructor to optionally generate full HTML file in one go if flag is true
    public WOW(String name, boolean flag)
    {
        addWrapper = flag;
        buildTemplates();
        setup(name);

        if (addWrapper) { write(startHTML.toString()+"\n"); }
        write(head.toString()+"\n");

        if (addWrapper) { write(endHead.toString()+"\n"); }
        write(body.toString()+"\n");

        if (addWrapper) { write(endBody.toString()+"\n"); }
    } // end of constructor
    
    
    // find what path we are executing in and remove pre-existing file of same name
    def buildTemplates()
    {
        dta += make(title:"Lake",href:"Lake.com",imgsrc:"lake.jpg",alt:"This is a lake",imagenumber:imagenumber);
        dta += make(title:"Landscape",alt:"landscape",href:"Landscape.com",imgsrc:"landscape.jpg",imagenumber:imagenumber);
        dta += make(title:"Sunset",href:"Sunset.com",imgsrc:"sunset.jpg",imagenumber:imagenumber);
        dta.each{e-> say "dta="+e; 
            sb << '\n'+e.toString();
            bullets << '\n'+e.toBullet();
        }
        say "StringBuffer bullets="+bullets.toString();
    } // end of constructor

    // find what path we are executing in and remove pre-existing file of same name
    def setup(String name)
    {
        File f = new File(".");
        say("Path="+f.getPath());
        say("Abs.Path="+f.getAbsolutePath());
        try {
            cp = f.getCanonicalPath();
            say("Canonical path="+cp);
        }
        catch(Exception e) {}

        say "File.separator="+File.separator;
        def fn = cp+File.separator+name
        say "fn="+fn;
        fo = new File(fn);
        yn = fo.exists();
        if (yn) { fo.delete() }
    } // end of setup()

    // return a Dta object bean with data for each template
    def make(def d)
    {
        def dd = new Dta(d);
        ++imagenumber;
        return dd;
    }  // end of make()


    // append text to existing file
    def write(String txt)
    {
        fo.append(txt);
    } // end of write()    


    // utility logging
    def say(def txt) { if (audit) println txt; }

    def startHTML = """<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<!--[if IE]><meta http-equiv="X-UA-Compatible" content="IE=edge"><![endif]-->
<meta name="viewport" content="width=device-width, initial-scale=1.0">"""

    def endHead = """</head>
<body>"""

    def endBody = """</body>
</html>"""


    // HEAD template for WOW slider is copied into output .html but you need to get the WOW pieces of .js and .css
    def head = """<!-- Start WOWSlider.com HEAD section -->
<link rel="stylesheet" type="text/css" href="engine1/style.css" />
<script type="text/javascript" src="engine1/jquery.js"></script>
<!-- End WOWSlider.com HEAD section -->"""

    // feeds LI lines for each image and companion bullet LI
    def body = """<!-- Start WOWSlider.com BODY section -->
<div id="wowslider-container1">
  <div class="ws_images">
    <ul>${sb}
    </ul>
  </div>
  
  <div class="ws_bullets">
    <div>${bullets}    
    </div>
  </div>
  
  <div class="ws_shadow"></div>
</div>    
<script type="text/javascript" src="engine1/wowslider.js"></script>
<script type="text/javascript" src="engine1/script.js"></script>
<!-- End WOWSlider.com BODY section -->
"""
    // main method
    public static void main(String[] args)
    {
        WOW w = new WOW();
        w.setup('kids.adoc');
        w.write(w.head.toString()+"\n");
        w.write(w.body.toString()+"\n");
        
        w = new WOW('fred.adoc');

        w = new WOW('max.html', true);

        w.say "--- the end ---"
    } // end of main
    
} // end of class