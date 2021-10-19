# WaIP - get party IP addrs
(c) leosol 2021

With the help of [jpclaudino](https://github.com/jpclaudino), jkoya and opsmaciel


## Main idea
Its known that, besides the fact that every WhatsApp call is encrypted, sometimes packets carrying call data go through a kind of STUN/TURN protocol.

This has been reported by [Marvin Schirrmacher](https://medium.com/@schirrmacher/analyzing-whatsapp-calls-176a9e776213).

There's also something about it at [ntop project](https://github.com/ntop/nDPI/issues/530)

And also [here](https://techbriefly.com/2021/07/14/this-security-flaw-in-whatsapp-could-leak-your-ip-address/) 

As a consequence, sometimes, **it's possible to identify each party IP address**. 

# WaIP tool
It was not only possible to check this, but we built a tool that can be used to try to identify each party IP address.
As the picture shows, it's possible to be ready to identify parties IP addresses during a call.

![Summary](pics/summary.png)

# Forensic Evidence
WhatsApp leaves a forensic evidence of the IP address used during the call handled by STUN/TURN.
See the binding info that is recorded in the WhatsApp internal logs. When STUN/TURN succeds, **WhatsApp registers a 0x102 code** in it's internal log files.
For the forensic examiner, this might be very usefull since sometimes each party phone number is known, but the investigators have no idea of a physical address. In this case, finding a party IP address can be very useful.

![Forensic Evidence](pics/whatsapp-log.JPG)

# Download
[waip-2021-10-13.apk](/dist/waip-2021-10-13.apk)

# Install
- Phone must be rooted
- Needs to allow adb install and not verify apps installed by usb
- ``adb install waip.apk``

# First Filter

``tshark -r capture.pcap stun|grep 'Binding'``

``grep -Eio 'Local:(.*), Remote:(.*), priority: 0x102' /data/data/com.whatsapp/files/Logs/whatsapp.log``

# Second Filter

``tshark -r /sdcard/capture.pcap -o gui.column.format:Source,%us,source port,%uS,Destination,%ud,dest port,%uD (ip.src==192.168.0.50&&ip.dst==10.61.102.80)||(ip.src==192.168.0.50&&ip.dst==200.173.225.24)``

# Limitations

It's not true that every uses STUN/TURN, so this depends on your network and the other party network. This means: do not expect to have the other party IP address every time.
Actually, while taking the screenshots for this doc, I had to try a few times.
