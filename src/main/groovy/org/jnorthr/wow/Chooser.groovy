package org.jnorthr.wow;
// groovy sample to choose one file using java's  JFileChooser
// would only allow choice of a single directory by setting another JFileChooser feature
// http://docs.oracle.com/javase/tutorial/uiswing/components/filechooser.html
// see more examples in above link to include a file filter
// fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
// fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
// **************************************************************

import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

// need these as copied from Dropbox/Projects/LoggingExamples
import org.slf4j.*
import groovy.util.logging.Slf4j
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* The Chooser program implements a support application that
* allows user to pick a single file or single folder directory.
*
* Initially starts to choose artifacts from program working directory and saves user
* choice of path in a local text file 
*
* Use annotation to inject log field into the class.
*
* @author  jnorthr
* @version 1.0
* @since   2016-08-01 
*/
@Slf4j
public class Chooser 
{
    /**
     * An Slf4J logger to show log messages.
     */
    Logger logger = LoggerFactory.getLogger(Chooser.class);

    /**
     * The kind of JFileChooser to show the user.
     */
    boolean openOrSave = true;

    /**
     * True if the user can select a single local file artifact.
     */
    boolean fileSelect = true;

    /**
     * True if the user can select a single local directory folder artifact.
     */
    boolean pathSelect = true;

    /**
     * True of the user actually chose a local file artifact.
     */
    boolean chosen = false;

    /**
     * A path value to influence the JFileChooser as where to allow the user to initially pick a local file artifact.
     * Can be over-written by a value chosen in the previous run of this module
     */
    def initialPath = System.getProperty("user.dir");


    /**
     * Parent component of the dialog.
     */
    JFrame frame = new JFrame();
    
    
    /**
     * Handle to component used by the chooser dialog.
     */
    JFileChooser fc = null;
    
    
    /**
     * Integer value to influence the dialog of what's allowed in the user's inter-action with the chooser. 
     * For example: JFileChooser.FILES_AND_DIRECTORIES, JFileChooser.DIRECTORIES_ONLY, JFileChooser.FILES_ONLY 
     */
    java.lang.Integer mode = JFileChooser.FILES_AND_DIRECTORIES;


    /**
     * Temp work area holding a default file path and file name. This name points to a cache where the selection 
     * from a prior run is stored.  
     */
    String rememberpath = System.getProperty("user.home") + File.separator  +".chooser.txt";


    /**
     * This is the title to appear at the top of user's dialog. It confirms what we expect from the user.  
     */
    String menuTitle = "Make a Selection";
    
    
    // ==============================================
    // Following values set after user choice is made
    
    /**
     * Integer indicator of the user's inter-action with the chooser. For example: JFileChooser.APPROVE_OPTION
     */
    int result = -1;

    /**
     * Temp work area holding the absolute path to the user's artifact selected with the chooser. 
     * For example: fc.getCurrentDirectory().getAbsolutePath() 
     */
    def path = null;


    /**
     * Temp work area holding only the name of the file the user's artifact selected with the chooser, 
     * but not it's path. Might not hold a value when Directory_Only choices are in effect.
     */
    def artifact = null;
    
    /**
     * Temp work area holding the full and complete absolute path and file name of the user's artifact 
     * selected with the chooser. 
     */
    def fullname = null;


   /** 
    * Class constructor.
    * defaults to let user pick either a file or a folder
    */
    public Chooser()
    {
    	logger.info("this is an .info msg from the Chooser default constructor");
        setup();
    } // endof constructor
    
   /** 
    * Ask JFileChooser to only allow user to pick a local file but not folder.
    */
    public void setTitle(String newTitle)
    {
    	logger.info("setTitle(String ${newTitle})");
        menuTitle = newTitle;
        fc.setDialogTitle(menuTitle);
    } // end of method

   /** 
    * Influences JFileChooser to use either the 'Open' dialog if true or the 'Save' dialog if false.
    */
    public void setOpenOrSave(boolean oos)
    {
    	logger.info("setOpenOrSave(String ${oos})");
        openOrSave = oos;
    } // end of method
    
   /** 
    * Ask JFileChooser to only allow user to pick a local file but not folder.
    */
    public void selectFileOnly()
    {
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        pathSelect = false;
        fileSelect = true;
    } // endof method


   /** 
    * Ask JFileChooser to only allow user to pick a local directory folder but not a file.
    */
    public void selectFolderOnly()
    {
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileSelect = false;
        pathSelect = true;
    } // endof method


   /** 
    * Ask JFileChooser to only allow user to pick a local directory folder but not a file.
    */
    public void selectBoth()
    {
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileSelect = true;
        pathSelect = true;
    } // endof method
    
    
   /**
    * Method to prepare class variables by reading a possibly non-existent cache file written in prior run.
    */
    public void setup()
    {
        boolean present = new File(rememberpath).exists()
        if (present) { initialPath = new File(rememberpath).getText(); }

        fc = new JFileChooser();
        mode = JFileChooser.DIRECTORIES_ONLY;

        if (pathSelect)
        {
            if (fileSelect)
            {
                mode = JFileChooser.FILES_AND_DIRECTORIES;
            }
            else
            {
                mode = JFileChooser.DIRECTORIES_ONLY;
            }
        }
        else
        {
            mode = JFileChooser.FILES_ONLY;
        }       

        fc.setFileSelectionMode(this.mode);
        fc.setDialogTitle(menuTitle);

        File workingDirectory = new File(initialPath); 
        fc.setCurrentDirectory(workingDirectory);
    	logger.info("setup changed fc.setCurrentDirectory to ${initialPath}");
        
    } // endof setup


   /** 
    * Influence JFileChooser to allow user selection to begin from a known local folder.
    */
    public void setPath(String newPath)
    {
        initialPath = newPath;
        File workingDirectory = new File(initialPath);
        if ( !workingDirectory.exists() ) { throw new RuntimeException("Cannot setPath to non-existence path:"+newPath)} 
        fc.setCurrentDirectory(workingDirectory);
    } // endof setup


    /**
     * Returns a boolean to indicate what the user did in the JFileChooser dialog. 
     * 
     * argument is a string specifier of the title to show the user on the dialog. 
     * 
     * This method always returns true if user clicked the APPROVE button indicating 
     * an actual choice was made else returns false if user aborted and failed to make a choice.
     *
     * @param  menuname the title of the dialog shown to the user
     * @return boolean true if user clicked the APPROVE button
     *                false if user did not make a choice
     */
    public boolean getChoice()
    {
        if (!openOrSave) { fc.setSelectedFile(new File("fileToSave.txt")); }
        result = (!openOrSave) ? fc.showSaveDialog(frame) : fc.showOpenDialog(frame) ;
        chosen = false;
        switch ( result )
        {
            case JFileChooser.APPROVE_OPTION:
                  File file = fc.getSelectedFile();
                  path =  fc.getCurrentDirectory().getAbsolutePath();
                  artifact=file.name;
                  fullname = file.toString();
                  //println "APPROVE path="+path+" artifact="+artifact+" fullname="+fullname+" rememberpath="+rememberpath
                  def fo = new File(rememberpath)
                  fo.text = path; 
                  chosen = true;
                  break;

            case JFileChooser.CANCEL_OPTION:
            case JFileChooser.ERROR_OPTION:
                  chosen = false;
                  break;
        } // end of switch
        
        return chosen;
    } // end of pick
    
    
    /**
     * The primary method to execute this class. Can be used to test and examine logic and performance issues. 
     * 
     * argument is a list of strings provided as command-line parameters. 
     * 
     * @param  args a list of possibly zero entries from the command line
     */
    public static void main(String[] args)
    {
        def ch = new Chooser();
        ch.setOpenOrSave(false);
        ch.setTitle("Pick a Folder and Filename to save");
        if (ch.getChoice())
        {
                  println "path="+ch.path+"\nartifact name="+ch.artifact.toString();    
                  println "the full name of the selected file is "+ch.fullname;    
        }
/*        
        ch = new Chooser(true);
        if (ch.getChoice())
        {
                  println "path="+ch.path+"\nfile name="+ch.artifact.toString();    
                  println "the full name of the selected file is "+ch.fullname;    
        }

        ch = new Chooser(false);
        if (ch.getChoice())
        {
                  println "path="+ch.path+"\nfile name="+ch.artifact.toString();    
                  println "the full name of the selected file is "+ch.fullname;    
        }
*/
       System.exit(0);
    } // end of main

    
    
} // end of class