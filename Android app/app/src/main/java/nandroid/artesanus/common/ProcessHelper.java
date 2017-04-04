package nandroid.artesanus.common;

/**
 * Helper class for Processes:
 */
public class ProcessHelper
{

    // Enumerate with the diferent proceses inside crafting beer process.
    public enum CRAFTING_PROCESS
    {
        ESE,
        OSA,
        ISO,
        NONE;
    };

    // This pretend to mark us the workflow process
    public static CRAFTING_PROCESS nextProcess(CRAFTING_PROCESS crafting)
    {
        CRAFTING_PROCESS retVal = CRAFTING_PROCESS.NONE;
        if (crafting.compareTo(CRAFTING_PROCESS.ESE)== 0)
        {
            retVal = CRAFTING_PROCESS.ISO;
        }
        else if(crafting.compareTo(CRAFTING_PROCESS.ISO)== 0)
        {
            retVal = CRAFTING_PROCESS.OSA;
        }
        else if(crafting.compareTo(CRAFTING_PROCESS.OSA)== 0)
        {
            retVal = CRAFTING_PROCESS.NONE;
        }

        return retVal;
    };

}


