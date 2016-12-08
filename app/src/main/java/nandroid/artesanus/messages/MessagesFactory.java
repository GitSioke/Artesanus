package nandroid.artesanus.messages;

/**
 * Created by Nando on 07/12/2016.
 */

import java.util.Date;

/**
 * This Abstract Factory defines creation methods for Message child classes.
 */

public interface MessagesFactory
{
    MessageHeating createMessageHeating(float processId, int priority, Date date);
    MessageFermenter createMessageFermenter(float processId, int priority, Date date);
    MessageMashing createMessageMashing(float processId, int priority, Date date);
}
