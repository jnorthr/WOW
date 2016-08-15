//@Grab('log4j:log4j:1.2.17')  use this when running outside gradle or groovyConsole

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

import org.apache.log4j.*
import groovy.util.logging.*  

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
@Log4j
public class Chooser 
{
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
     * Can be over-written by a value chosen in the previous run of this module. See 'rememberpath' below
     */
    def initialPath = System.getProperty("user.dir");

    /**
     * A path value to influence the JFileChooser as where to allow the user to initially pick a local file artifact.
     * Can be over-written by a value chosen in the previous run of this module. See 'rememberpath' below
     */
	def initialFile = "fileToSave.txt";

	
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
     * Temp work area holding a default file path and file name. This name points to a cache where the selected 
     * path from a prior run is stored.  
     */
    String rememberpath = System.getProperty("user.home") + File.separator  +".path.txt";


    /**
     * Temp work area holding a default file path and file name. This name points to a cache where the selected 
     * full filename from a prior run is stored.  
     */
    String rememberfile = System.getProperty("user.home") + File.separator  +".file.txt";


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
	 *
	 * example choosing a file artifact:
	 * APPROVE path=/Users/jimnorthrop/Dropbox/Projects/Web/config artifact=logback.xml 
	 * fullname=/Users/jimnorthrop/Dropbox/Projects/Web/config/logback.xml 
	 * rememberpath=/Users/jimnorthrop/.path.txt 
	 * isDir=false
     */
    def path = null;


    /**
     * Temp work area holding only the name of the file the user's artifact selected with the chooser, 
     * but not it's path. Holds a value when Directory_Only choice is in effect of lowest level folder name
     * and parent path is in 'path' variable above.
     */
    def artifact = null;
    
    /**
     * Temp work area holding the full and complete absolute path plus file name of the user's artifact 
     * selected with the chooser. 
     */
    def fullname = null;

    /**
     * Flag set when name of the user's artifact 
     * selected with the chooser is a folder directory 
     */
    boolean isDir = false;


   // =========================================================================
   /** 
    * Class constructor.
    * defaults to let user pick either a file or a folder
    */
    public Chooser()
    {
    	log.info("this is an .info msg from the Chooser default constructor");
        setup();
    } // endof constructor
    
   /** 
    * Ask JFileChooser to only allow user to pick a local file but not folder.
    */
    public void setTitle(String newTitle)
    {
    	log.info("setTitle(String ${newTitle})");
        menuTitle = newTitle;
        fc.setDialogTitle(menuTitle);
    } // end of method

   /** 
    * Influences JFileChooser to use either the 'Open' dialog if true or the 'Save' dialog if false.
    */
    public void setOpenOrSave(boolean oos)
    {
    	log.info("setOpenOrSave(String ${oos})");
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
        present = new File(rememberfile).exists()
        if (present) { initialFile = new File(rememberfile).getText(); }

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
    	log.info("setup changed fc.setCurrentDirectory to ${initialPath}");
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
    } // endof setPath


	// =============================================================================
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
        if (!openOrSave) { fc.setSelectedFile(new File(initialFile)); }
        result = (!openOrSave) ? fc.showSaveDialog(frame) : fc.showOpenDialog(frame) ;
        chosen = false;
        switch ( result )
        {
            case JFileChooser.APPROVE_OPTION:
                  File file = fc.getSelectedFile();
                  path =  fc.getCurrentDirectory().getAbsolutePath();
                  artifact=file.name;
                  fullname = file.toString();
                  boolean isDir = new File(fullname).isDirectory();
                  log.info "APPROVE path="+path+" artifact="+artifact+" fullname="+fullname+" rememberpath="+rememberpath+" isDir="+isDir;
                  
                  def fo = new File(rememberpath)
                  fo.text = (isDir) ? fullname : path;
				  fo = new File(rememberfile)
                  fo.text = (isDir) ? "" : artifact;
                   
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
     * to get user selection of path of a known local folder.
     */
    public String getPath()
    {
    	return path;
    } // end of getPath


    /** 
     * To get user selection of file but not path of a known local folder.
     */
    public String getFile()
    {
    	return artifact;
    } // end of getFile

    /** 
     * To get user selection of  full name of a known local folder.
     */
    public String getName()
    {
    	return fullname;
    } // end of getName

    
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
/*        
        ch.log.info "trying a SAVE feature"
        ch.setOpenOrSave(false);
        ch.setTitle("Pick a Folder and Filename to save");
        if (ch.getChoice())
        {
        	ch.log.info  "path="+ch.path+"\nartifact name="+ch.artifact.toString();    
            ch.log.info  "the full name of the selected file is "+ch.fullname;    
        }

        ch = new Chooser();
        ch.log.info "trying the default feature"
        if (ch.getChoice())
        {
            ch.log.info  "path="+ch.path+"\nfile name="+ch.artifact.toString();    
        	ch.log.info  "the full name of the selected file is "+ch.fullname;    
        }
*/
        ch = new Chooser();
        ch.log.info "trying to pick a folder-only feature"
        ch.setTitle("Pick input Folder");
        ch.selectFolderOnly()
        if (ch.getChoice())
        {
            ch.log.info  "path="+ch.path+"\nfile name="+ch.artifact.toString();    
            ch.log.info  "the full name of the selected file is "+ch.fullname;    
        }
        
       System.exit(0);
    } // end of main

    
    
} // end of class