# WaIP - get party IP addrs
(c) leosol 2021

With the help of [jpclaudino](https://github.com/jpclaudino), jkoya and opsmaciel


## Main idea
Its known that, besides the fact that every WhatsApp call is encrypted, it's known that sometimes packets carrying call data go through a kind of STUN/TURN protocol.
This has been reported by [Marvin Schirrmacher](https://medium.com/@schirrmacher/analyzing-whatsapp-calls-176a9e776213).

As a consequence, sometimes, **it's possible to identify each party IP address**. 

# WaIP tool
It was not only possible to check this, but we could build a tool that can be used to try to identify each party IP address.
As the picture shows, it's possible to be ready to identify parties IP addresses during a call.

![Summary](pics/summary.png)

# Forensic Evidence
WhatsApp leaves a forensic evidence of the IP address used during the call handled by STUN/TURN.
See the binding info that is recorded in the WhatsApp internal logs

![Forensic Evidence](pics/whatsapp-log.JPG)
