package com.example.testapp;

public class TODO {
	/*
	 	
		TODO: Kolla så att inga uppkopplingar ligger igång efter att rss är hämtad
		TODO: Fixa så att appen inte krashar om man saknar internetuppkoppling.
	 	TODO: Gör 9 patch knappar för focus och focus/pressed
	 	TODO: Spara ner de senaste nerladdade nyheterna så om uppkoppling saknas läses de gamla in.
	 	TODO: Gör så att man kan refresha nyheter utan att gå ut och tillbaka
	 	TODO: Ändra EMSG_NO_INTERNET_CONNECTION
	 	
	 	(SQLite är kanske lite overkill för denna appen, kommer inte innehålla några större mängder data)
	 	Kan spara ner ArrayList som Set och sen när man tar tillbaka de gör man en ny ArrayList och sorterar
	 	Kan tex spara ner i-värdet i varje post eller liknande för att sen kunna sortera de när de e i set
	 	//Retrieve the values
		Set<String> set = new HashSet<String>();
		set = myScores.getStringSet("key", null);
		
		//Set the values
		Set<String> set = new HashSet<String>();
		set.addAll(listOfExistingScores);
		scoreEditor.putStringSet("key", set);
		scoreEditor.commit();

	*/
}
