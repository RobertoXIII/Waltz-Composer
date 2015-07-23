/**
 * SimpleMidiWriter.java
 * Code for saving a MIDI file using only
 * a list of Integer pitches and durations.
 */

// these includes will allow us to write to a midi file
import java.io.File;
import java.io.IOException;

import javax.sound.midi.Sequence;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import javax.sound.midi.InvalidMidiDataException;

import java.util.ArrayList;

public class SimpleMidiWriter
{
	
	// some constants that define NOTE_ON and NOTE_OFF events
	public static final int NOTE_ON = 0x90;
	public static final int NOTE_OFF = 0x80;
	
	// constants that define other events
	public static final int TIME_SIGNATURE = 0x58;
	public static final int META = 0xFF;
	public static Note key;
	public static int tone = 48;
	public static int lastTick;
	
	// time resolution of midi file
	public static int TIME_RESOLUTION = 120;
	// possible note durations
	public static final int[] durations = {30, 60, 90, 120, 180, 240, 360, 480};
	
	public static String saveToMidiFile(String Filename, ArrayList<Note> notes)
	{
		int newPitch;
		int newStart = 0;
		int newEnd = 0;
		
		/*
		if (key.pitch > 70 )
		{
			key.pitch -= 24;
		}*/
		 /*
		switch (tone) {
		
		case 0:
			tone = 48;
			break;
			
		case 1: 
			tone = 55;
			break;
		
		case 2: 
			tone = 50;
			break;
			
		case 3: 
			tone = 45;
			break;
			
		case 4: 
			tone = 52;
			break;
			
		case 5: 
			tone = 59;
			break;
			
		case 6: 
			tone = 54;
			break;
			
		case 7:
			break;
		}
		*/
		
		Sequence s = null;
		try
		{
			s = new Sequence(Sequence.PPQ, TIME_RESOLUTION); // create a new sequence
		}
		catch (InvalidMidiDataException e)
		{
			return e.getMessage();
		}
		
		Track track = s.createTrack();
		 
		// add the notes in the ArrayList  to the track
		for( int i = 0; i < notes.size(); i++ )
		{

			if (newStart == 0)
			{
				newEnd = notes.get(i).durationSource;
			}
			
			newPitch = notes.get(i).pitch;
			newStart = newEnd;
			newEnd = newStart + notes.get(i).durationSource;
			
			if( newPitch > -1 ) // -1 = rest
			{
				track.add(CreateNoteEvent(ShortMessage.NOTE_ON, newPitch, 96, newStart));
				track.add(CreateNoteEvent(ShortMessage.NOTE_OFF, newPitch, 0, newEnd));
			}
		}
		
		int currentTick = 0;
		int bar = TIME_RESOLUTION * 3;
		
		while (currentTick < lastTick)
		{
			track.add(CreateNoteEvent(ShortMessage.NOTE_ON, tone, 96, currentTick));
			track.add(CreateNoteEvent(ShortMessage.NOTE_OFF, tone, 0, currentTick + bar));
			
			track.add(CreateNoteEvent(ShortMessage.NOTE_ON, tone + 4, 96, currentTick + TIME_RESOLUTION));
			track.add(CreateNoteEvent(ShortMessage.NOTE_OFF, tone + 4, 0, currentTick + (TIME_RESOLUTION * 2)));
			track.add(CreateNoteEvent(ShortMessage.NOTE_ON, tone + 7, 96, currentTick + TIME_RESOLUTION));
			track.add(CreateNoteEvent(ShortMessage.NOTE_OFF, tone + 7, 0, currentTick + (TIME_RESOLUTION * 2)));
			
			track.add(CreateNoteEvent(ShortMessage.NOTE_ON, tone + 4, 96, currentTick + (TIME_RESOLUTION * 2)));
			track.add(CreateNoteEvent(ShortMessage.NOTE_OFF, tone + 4, 0, currentTick + (TIME_RESOLUTION * 3)));
			track.add(CreateNoteEvent(ShortMessage.NOTE_ON, tone + 7, 96, currentTick + (TIME_RESOLUTION * 2)));
			track.add(CreateNoteEvent(ShortMessage.NOTE_OFF, tone + 7, 0, currentTick + (TIME_RESOLUTION * 3)));
			
			track.add(CreateNoteEvent(ShortMessage.NOTE_ON, tone - 5, 96, currentTick + bar));
			track.add(CreateNoteEvent(ShortMessage.NOTE_OFF, tone - 5, 0, currentTick + (bar * 2)));
			
			track.add(CreateNoteEvent(ShortMessage.NOTE_ON, tone + 4, 96, currentTick + TIME_RESOLUTION + bar));
			track.add(CreateNoteEvent(ShortMessage.NOTE_OFF, tone + 4, 0, currentTick + (TIME_RESOLUTION * 2) + bar ));
			track.add(CreateNoteEvent(ShortMessage.NOTE_ON, tone + 7, 96, currentTick + TIME_RESOLUTION + bar));
			track.add(CreateNoteEvent(ShortMessage.NOTE_OFF, tone + 7, 0, currentTick + (TIME_RESOLUTION * 2) + bar ));
			
			track.add(CreateNoteEvent(ShortMessage.NOTE_ON, tone + 4, 96, currentTick + (TIME_RESOLUTION * 2) + bar));
			track.add(CreateNoteEvent(ShortMessage.NOTE_OFF, tone + 4, 0, currentTick + (TIME_RESOLUTION * 3) + bar));
			track.add(CreateNoteEvent(ShortMessage.NOTE_ON, tone + 7, 96, currentTick + (TIME_RESOLUTION * 2) + bar));
			track.add(CreateNoteEvent(ShortMessage.NOTE_OFF, tone + 7, 0, currentTick + (TIME_RESOLUTION * 3) + bar));
			
			currentTick += bar * 2;
		}

		track.add(CreateNoteEvent(ShortMessage.NOTE_ON, tone , 96, currentTick));
		track.add(CreateNoteEvent(ShortMessage.NOTE_OFF, tone , 0, currentTick + bar));
		track.add(CreateNoteEvent(ShortMessage.NOTE_ON, tone + 4, 96, currentTick));
		track.add(CreateNoteEvent(ShortMessage.NOTE_OFF, tone + 4, 0, currentTick  + bar));
		track.add(CreateNoteEvent(ShortMessage.NOTE_ON, tone + 7, 96, currentTick));
		track.add(CreateNoteEvent(ShortMessage.NOTE_OFF, tone + 7, 0, currentTick + bar));
		
	    try
	    {
	    	File OutputFile = new File(Filename);
	    	// 1 = multitrack midi file
	    	MidiSystem.write(s, 1, OutputFile);
	    }
	    catch (IOException e)
	    {
	    	return e.getMessage();
	    }
	    return "";
	}
	
	/**
	 * Create a midi event
	 * 
	 * @param nCommand NOTE_ON, NOTE_OFF, etc
	 * @param nKey 0-127
	 * @param nVelocity 0-127
	 * @param lTick the starting tick, as a count of PulsesPerQuarterNote
	 * @return a new MidiEvent
	 */
	private static MidiEvent CreateNoteEvent(int nCommand, int nKey, int nVelocity, long lTick)
	{
	  ShortMessage  message = new ShortMessage();
	  try
	  {
	    message.setMessage(nCommand, 0,  nKey, nVelocity);
	  }
	  catch (InvalidMidiDataException e)
	  {
	    e.printStackTrace();
	    System.exit(1);
	  }
	  MidiEvent event = new MidiEvent(message, lTick);
	  return event;
	}
}
