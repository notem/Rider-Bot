package ws.nmathe.rider.core;

import java.io.*;
import java.util.Properties;

/**
 * contains configurable variables for the bot
 */
public class BotSettings
{
    private static final String FILENAME = "rider.properties";
    private static final String DEFAULT_TOKEN = "BOT_TOKEN";
    private static final String DEFAULT_ADMIN_ID = "ADMIN_USER_ID";
    private static final String DEFAULT_COMMAND_PREFIX = ":";
    private static final String DEFAULT_ADMIN_COMMAND_PREFIX = ".";

    private static final String DEFAULT_EXPIRE = "1h";
    private static final String DEFAULT_CHANNEL = "lfg";

    private Properties properties;

    public static BotSettings init()
    {
        BotSettings bc = new BotSettings();
        bc.properties = new Properties();
        InputStream input = null;

        try
        {
            input = new FileInputStream("./" + FILENAME);

            //load a properties file from class path, inside static method
            bc.properties.load(input);
        }
        catch (IOException ex)
        {
            generateFile();
            return null;
        }
        finally
        {
            if (input != null)
            {
                try
                {
                    input.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        return bc;
    }

    private static void generateFile()
    {
        Properties p = new Properties();
        OutputStream output = null;
        try
        {
            output = new FileOutputStream(FILENAME);

            // set the default values
            p.setProperty("token", DEFAULT_TOKEN);
            p.setProperty("admin_id", DEFAULT_ADMIN_ID);
            p.setProperty("command_prefix", DEFAULT_COMMAND_PREFIX);
            p.setProperty("admin_command_prefix", DEFAULT_ADMIN_COMMAND_PREFIX);
            p.setProperty("channel", DEFAULT_CHANNEL);
            p.setProperty("expire", DEFAULT_EXPIRE);

            // save properties to project root folder
            p.store(output, null);
        }
        catch (IOException io)
        {
            io.printStackTrace();
        }
        finally
        {
            if (output != null)
            {
                try
                {
                    output.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getToken()
    {
        return this.properties.getProperty("token");
    }

    public String getWebToken()
    {
        return this.properties.getProperty("webtoken");
    }

    public String getAdminId()
    {
        return this.properties.getProperty("admin_id");
    }

    public String getCommandPrefix()
    {
        return this.properties.getProperty("command_prefix");
    }

    public String getAdminPrefix()
    {
        return this.properties.getProperty("admin_command_prefix");
    }

    public String getChannel()
    {
        return this.properties.getProperty("channel");
    }

    public Integer getExpire()
    {
        Integer expire = 0;
        String expireProp = this.properties.getProperty("expire");
        String temp = "0";
        for( int i = 0; i < expireProp.length(); i++)
        {
            if( Character.isDigit( expireProp.charAt(i) ) )
            {
                temp += expireProp.charAt(i);
            }
            else
            {
                switch (expireProp.charAt(i))
                {
                    case 's' :
                        expire += Integer.parseInt(temp);
                        break;
                    case 'm' :
                        expire += 60 * Integer.parseInt(temp);
                        break;
                    case 'h' :
                        expire += 60*60 * Integer.parseInt(temp);
                        break;
                    case 'd' :
                        expire += 60*60*24 * Integer.parseInt(temp);
                        break;
                    case 'w' :
                        expire += 60*60*24*7 * Integer.parseInt(temp);
                        break;
                    default :
                        return expire;
                }
                temp = "0";
            }
        }
        return expire;
    }
}
