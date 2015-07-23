import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class WaltzGenerator
{
	public static void main(String[] args)
	{
		int rn;
		Random ran = new Random();
		String newNote = "";
		String prevNote ="";
		SimpleMidiReader smr = new SimpleMidiReader("test.mid");
		ArrayList<Note> notes = smr.getNotes();
		ArrayList<Note> possibleNotes = new ArrayList<>();
		ArrayList<Note> newSong = new ArrayList<>();
		HashMap<String, ArrayList<Note>> noteList = new HashMap<String, ArrayList<Note>>();
		HashMap<String, Double> noteProb = new HashMap<String, Double>();
		double probability = 0;
		int key;
		
		for( int i = 0; i < notes.size(); i++)
		{
			prevNote = newNote;
			newNote = notes.get(i).pitchName;
			
						
			if (newNote.length() > 0 && !noteList.containsKey(newNote))
			{
				noteList.put(newNote, new ArrayList<Note>());
				probability = 1 / notes.size();
				noteProb.put(newNote, probability);
			}
			

			if (prevNote.length() > 0 && newNote.length() > 0)
			{
				ArrayList<Note> tmp = noteList.get(prevNote);
				tmp.add(notes.get(i));
				noteList.put(prevNote, tmp);
				
				probability += 1 / notes.size();
				noteProb.put(newNote, probability);
			}
			
			/*
			//for debugging
			for (int j = 0; j < noteList.get(newNote).size(); j++)
			{
				System.out.println(noteList.get(newNote).get(j) + "  chors that follow");
			}*/
		}
		
		for(int j = 0; j < notes.size(); j++)
		{
			rn = ran.nextInt(notes.size());
			newNote = notes.get(rn).pitchName;
		
			
			if (noteList.containsKey(newNote))
			{
				possibleNotes = noteList.get(newNote);
				newSong.add(possibleNotes.get(ran.nextInt(possibleNotes.size())));
			}
			
		}
		
		
		// save them to a midi file
		SimpleMidiWriter smw = new SimpleMidiWriter();
		
		boolean counter = false;
		int timeRes = 0;
		
		for (int i = 0; i < notes.size() - 2; i++)
		{
			if (notes.get(i).durationName == notes.get(i + 1).durationName && notes.get(i).startTickSource != notes.get(i + 1).startTickSource)
			{
				timeRes = notes.get(i + 1).startTickSource - notes.get(i).startTickSource;
				
				if (notes.get(i).durationName == "half")
				{
					timeRes = timeRes / 2;
					counter = true;
				}
				
				else if (notes.get(i).durationName == "quarter")
				{
					timeRes = timeRes;
					counter = true;
				}
				
				else if (notes.get(i).durationName == "eighth")
				{
					timeRes = timeRes * 2;
					counter = true;
				}
				
				else if (notes.get(i).durationName == "sixteenth")
				{
					timeRes = timeRes * 4;
					counter = true;
				}
			}
			
			if (counter)
			{
				break;
			}
		}
		
		key = getTone(newSong, noteProb);
		
		smw.TIME_RESOLUTION = timeRes;
		smw.tone = key;
		smw.lastTick = getLastTick(newSong);
		smw.saveToMidiFile("output.mid", newSong);
	
		

		System.out.println("Done.");
	}
	
	public static Note getKey(ArrayList<Note> notes, HashMap<String, Double> prob)
	{
		String pitchName;
		Note n;
		Double highest = 0.0, current = 0.0;
		
		n = notes.get(0);
		
		for (int i = 0; i < notes.size(); i++)
		{
			pitchName = notes.get(i).pitchName;
			
			if (prob.containsKey(pitchName))
			{
				current = prob.get(pitchName);
			}
			
			if (current > highest)
			{
				highest = current;
				n = notes.get(i);
			}
		}
		
		
		return n;
	}
	
	public static int getTone(ArrayList<Note> notes, HashMap<String, Double> prob)
	{
		ArrayList<String> pitches = new ArrayList<>();
		int tone = 48;
		
		for (Note n : notes)
		{
			if (n.pitchName == "A#" || n.pitchName == "C#" || n.pitchName == "D#" || n.pitchName == "F#" || n.pitchName == "G#")
			{
				if (!pitches.contains(n.pitchName))
				{
					pitches.add(n.pitchName);
				}
			}
		}
		
		if (pitches.size() == 1 && pitches.contains("F#"))
		{
			tone = 55;
		}
		
		else if (pitches.size() == 1)
		{
			tone = 53;
		}
		
		if (pitches.size() == 2 && pitches.contains("C#"))
		{
			tone = 50;
		}
		
		else if (pitches.size() == 2)
		{
			tone = 58;
		}
		
		if (pitches.size() == 3 && pitches.contains("C#"))
		{
			tone = 45;
		}
		
		else if (pitches.size() == 3)
		{
			tone = 51;
		}
		
		if (pitches.size() == 4 && pitches.contains("F#"))
		{
			tone = 52;
		}
		
		else if (pitches.size() == 4)
		{
			tone = 56;
		}
		
		if (pitches.size() == 5)
		{
			tone = 59;
		}
		
		return tone;
	}
	
	public static int getLastTick(ArrayList<Note> notes)
	{
		int highest = 0;
		
		for (int i = 0; i < notes.size(); i++)
		{
			if (notes.get(i).endTickSource > highest)
			{
				highest = notes.get(i).endTickSource;
			}
		}
		
		return highest;
	}
}
