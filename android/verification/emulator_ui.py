"""Small read/tap/screenshot helper for manually reviewing the installed debug app."""
import subprocess, sys, re, xml.etree.ElementTree as ET, pathlib
ADB='/home/ezekdo/Android/Sdk/platform-tools/adb'
def adb(*args): return subprocess.check_output([ADB,*args])
def tree():
    adb('shell','uiautomator','dump','/sdcard/ctdrs-window.xml')
    return ET.fromstring(adb('shell','cat','/sdcard/ctdrs-window.xml'))
def tap(text):
    nodes=[n for n in tree().iter('node') if n.get('text')==text or n.get('content-desc')==text]
    if not nodes: raise RuntimeError('Not visible: '+text)
    x1,y1,x2,y2=map(int,re.findall(r'\d+',nodes[-1].get('bounds')))
    adb('shell','input','tap',str((x1+x2)//2),str((y1+y2)//2))
if __name__=='__main__':
    mode=sys.argv[1]
    if mode=='dump':
        for n in tree().iter('node'):
            if n.get('text') or n.get('content-desc') or n.get('class')=='android.widget.EditText': print(n.get('class'),repr(n.get('text')),repr(n.get('content-desc')),n.get('bounds'))
    elif mode=='tap': tap(sys.argv[2])
    elif mode=='shot': pathlib.Path(sys.argv[2]).write_bytes(adb('exec-out','screencap','-p'))
