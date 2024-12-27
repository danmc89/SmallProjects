stop crying edit
urls=(`ls *.url | awk '{print "\"" $0 "\"" }'`)

PropertiesFileLoader.java edit oops
if(ss.length >= 2)
{
  String urlVal = "";
  for(int i = 1; i <ss.length; i++)
  {
    urlVal += ss[i] + "=";
  }
  urlVal = urlVal.substring(0,urlVal.length()-1);
  props.put(ss[0], urlVal);
}
