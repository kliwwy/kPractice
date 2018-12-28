package me.wilk3z.kpractice.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class FileUtil
{
    public void copy(File source, File destination) throws Exception
    {
        if(source.isDirectory())
        {
            if(!destination.exists()) destination.mkdirs();
            for(String file : source.list()) copy(new File(source, file), new File(destination, file));
        }
        else copyFile(source, destination);
    }

    public void copyFile(File source, File destination) throws Exception
    {
        if(!destination.exists()) destination.createNewFile();
        FileChannel sourceChannel = null;
        FileChannel destinationChannel = null;
        try
        {
            sourceChannel = new FileInputStream(source).getChannel();
            destinationChannel = new FileOutputStream(destination).getChannel();
            destinationChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return;
        }
        if(source != null) sourceChannel.close();
        if(destination != null) destinationChannel.close();
    }

    public void delete(File file)
    {
        if(file.isDirectory())
        {
            for(String files : file.list()) delete(new File(file, files));
            file.delete();
        }
        else file.delete();
    }
}