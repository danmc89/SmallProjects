#!/bin/bash
function tkill()
{
	tlist | awk '{system("taskkill //F //PID " $2)}' 
}

function tlist()
{
	#[Precision is evga app]|[msi/dcv2/LEDKeeper2 is msi center]|[AsusAudioCenter]|[MicrosoftPhone]|[edge browser]|[]
	tasklist | egrep -i "Precision|msi|dcv2|AsusAudioCenter|PhoneExperienceHost|msedge|Firestorm"
}

tkill
