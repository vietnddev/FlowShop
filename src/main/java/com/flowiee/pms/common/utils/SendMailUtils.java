package com.flowiee.pms.common.utils;

import org.apache.log4j.Priority;
import org.jfree.util.Log;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SendMailUtils {

    public static String replaceTemplateParameter(String pTemplateContent, Map<String, Object> pParameterMap) {
        try
        {
            Pattern lvParameterPattern = Pattern.compile("\\[%(.*?)(_(.*?))?%\\]");
            Matcher lvMatcher = lvParameterPattern.matcher(pTemplateContent);

            StringBuffer lvContentBuilder = new StringBuffer();

            while (lvMatcher.find())
            {
                String lvParameterName = lvMatcher.group(1);
                String lvFormat = lvMatcher.group(3);
                //if (pParameterMap.containsKey(lvParameterName.toUpperCase()))
                if (pParameterMap.containsKey(lvParameterName))
                {
                    Object lvParameterValue = pParameterMap.get(lvParameterName);
                    String lvParameterStringValue = null;

                    // currently only support date format
                    if (lvFormat != null && lvParameterValue.getClass().equals(Date.class))
                    {
                        // make sure format string case is with correct syntax
                        lvFormat = lvFormat.replaceAll("Y", "y").replaceAll("m", "M").replaceAll("D", "d");

                        try
                        {
                            Date lvDateParameter = (Date) lvParameterValue;
                            SimpleDateFormat lvSDF = new SimpleDateFormat(lvFormat);
                            lvParameterStringValue = lvSDF.format(lvDateParameter);
                        }
                        catch (Exception e)
                        {
                            throw new Exception("Template: Date Parameter " + lvParameterName + " Format (" + lvFormat + ") is invalid");
                        }
                    }
                    else
                    {
                        lvParameterStringValue = CoreUtils.trim(lvParameterValue.toString());
                    }

                    lvMatcher.appendReplacement(lvContentBuilder, lvParameterStringValue);
                }
                else
                {
                    // write log warning
                    throw new Exception("Template: Parameter " + lvParameterName + " not defined");
                }
            }

            lvMatcher.appendTail(lvContentBuilder);

            return lvContentBuilder.toString();
        }
        catch (Exception e) {
            Log.log(Priority.WARN.toInt(), e);
            return pTemplateContent;
        }
    }
}