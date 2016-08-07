package org.jnorthr.wow;
// groovy sample to choose one file using java's  JFileChooser
// would only allow choice of a single directory by setting another JFileChooser feature
// http://docs.oracle.com/javase/tutorial/uiswing/components/filechooser.html
// see more examples in above link to include a file filter
// fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
// fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

// start to choose files from pwd
public class Chooser 
{
    boolean chosen = false;
    boolean found = false;    
    def initialPath = System.getProperty("user.dir");
    // parent component of the dialog
	JFrame frame = new JFrame();
    JFileChooser fc = null;
    int result = -1;
    def path = null;
    def filename = null;
    def fullname = null;
    java.lang.Integer mode = JFileChooser.FILES_AND_DIRECTORIES;

    // keep name of most recent folder (not file) chosen
    String userHome = System.getProperty("user.home");
    String rememberpath = userHome + File.separator  +".chooser.txt";
    boolean present = false;
                
    // defaults to let user pick either a file or a folder
    public Chooser()
    {
        present = new File(rememberpath).exists()
        if (present) { initialPath = new File(rememberpath).getText(); }
        fc = new JFileChooser();
        //fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);  // remove this to allow either file or dir choice
        File workingDirectory = new File(initialPath); 
        fc.setCurrentDirectory(workingDirectory);
        fc.setFileSelectionMode(this.mode);
    } // endof constructor
    
    // true = folders only choices
    // false = files only choices
    public Chooser(boolean picker)
    {
        present = new File(rememberpath).exists()
        if (present) { initialPath = new File(rememberpath).getText(); }

        fc = new JFileChooser();
        fc.setCurrentDirectory(new File(initialPath));
        if (picker)
        {
            mode = JFileChooser.DIRECTORIES_ONLY;
        }
        else
        {
            mode = JFileChooser.FILES_ONLY;
        }       
        fc.setFileSelectionMode(this.mode);

    } // endof constructor



    public getChoice(def menuname, boolean openOrSave)
    {
    	fc.setDialogTitle(menuname);
    	if (openOrSave) { fc.setSelectedFile(new File("fileToSave.txt")); }
        result = (openOrSave) ? fc.showSaveDialog(frame) : fc.showOpenDialog(frame) ;
        chosen = false;
        switch ( result )
        {
            case JFileChooser.APPROVE_OPTION:
                  File file = fc.getSelectedFile();
                  path =  fc.getCurrentDirectory().getAbsolutePath();
                  filename=file.name;
                  fullname = file.toString();
				  println "APPROVE path="+path+" filename="+filename+" fullname="+fullname+" rememberpath="+rememberpath
                  def fo = new File(rememberpath)
                  fo.text = path;  //fullname;
                  chosen = true;
                  break;

            case JFileChooser.CANCEL_OPTION:
            case JFileChooser.ERROR_OPTION:
                  chosen = false;
                  break;
        } // end of switch
        
        return chosen;
    } // end of pick
    
    public static void main(String[] args)
    {
        def ch = new Chooser();
        if (ch.getChoice('Select an Output Folder',true))
        {
                  println "path="+ch.path+"\nfile name="+ch.filename.toString();    
                  println "the full name of the selected file is "+ch.fullname;    
        }
/*        
        ch = new Chooser(true);
        if (ch.getChoice())
        {
                  println "path="+ch.path+"\nfile name="+ch.filename.toString();    
                  println "the full name of the selected file is "+ch.fullname;    
        }

        ch = new Chooser(false);
        if (ch.getChoice())
        {
                  println "path="+ch.path+"\nfile name="+ch.filename.toString();    
                  println "the full name of the selected file is "+ch.fullname;    
        }
*/
		System.exit(0);
    } // end of main

    
    
} // end of class