/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package task;

import java.util.Map;

/**
 *
 * @author FallenShard
 */
public class CheckEditorTask implements Task 
{
    private String m_input = "";
    private Map<Integer, String> m_editors = null;
    
    private String m_result = "";
    
    public CheckEditorTask(String input, Map<Integer, String> editors)
    {
        m_input = input;
        m_editors = editors;
    }
    
    @Override
    public void run()
    {
        try
        {
            int docId = Integer.parseInt(m_input);
            
            if (m_editors.containsKey(docId))
            {
                m_result = "false";
            }
            else
            {
                m_result = "true";
            }
        }
        catch (Exception ex)
        {
            m_result = "false";
            ex.printStackTrace();
        }
    }

    @Override
    public String getResult()
    {
        return m_result;
    }
}
