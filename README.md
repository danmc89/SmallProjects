-------------------------

For RokuLauncher Project:
stop crying edit
urls=(`ls *.url | awk '{print "\"" $0 "\"" }'`)

Fix broken URLs
PropertiesFileLoader.java edit oops (forgot to add "2" limit as = can be part of url)
String [] ss = s.split(delimter,2);

Fix Scolling
in RokuLauncherWindow.buildInnerPanels ?-> scrPane.getVerticalScrollBar().setUnitIncrement(15);

Edit Extendable.properties
to create additional folders for adding content like "Youtube Free with ads" videos

-------------------------
