----------------------------------------------------------------
Avisynth 2.5.8 MT and x64 (Built on 3/09/2010 with ICC 11) by JoshyD 
gf----------------------------------------------------------------

+ Originally by Ben Rudiak-Gould et al (http://www.avisynth.org)

+ Modified to with Support for MT Mode by SEt

+ Further hacked apart to run in 64bit mode by JoshyD

+ Check the changes section of this document for major revision notes

----------------------------------------------------------------
Installation Directions
----------------------------------------------------------------

1. Be running a copy of 64 bit windows (Vista and 7 should both work, this was compiled with the Win7 SDK with vista compatibility settings, I'm not sure about XP64)

2. Have the 32 bit version of Avisynth installed to begin with, it's installer will setup all the system paths and registry entries

3. Copy the included avisynth.dll into your C:\Windows\System32 directory
	-I know it's backwards, but SysWOW64 stands for system windows on windows 64, it really contains all your 32bit binaries
	-You do not need to remove or change C:\Windows\SysWOW64\avisynth.dll in any way, windows will redirect apps to either binary independently
4. Copy included DevIL.dll into your C:\Windows\System32 directory
	-This is from the open source project Developer's Image Library
	-Fully available from http://openil.sourceforge.net/
	-If downloading on your own be sure to GET THE x64 VERSION of the library / dll

5. Run the batch installer avisynth_intall.cmd *AS ADMINISTRATOR* courtesy of turbojet from the doom9 forums
	-Right click the installer and choose "run as administrator" from the drop down menu
	-This will copy all Avisynth registry keys used by Avisynth32 to the locations needed by Avisynth64
	-It will also change your plugin autoload directory for avisynth64 to $Avisynth_DIR\plugins64
	-A fallback installation method can still be accomplished by adding the included avisynth.reg keys to your system's registry


6. This DLL will only work with 64 bit programs, and 64 bit codecs
	-Virtual Dub has a 64 bit version that will load avs files via this dll
		+download it at: http://virtualdub.sourceforge.net/ be sure to get the 64bit version, second section down
	-64 bit vfw codecs are required, a good place ot start is: http://sourceforge.net/projects/ffdshow-tryout/
		+Once again, be sure to download a build that was COMPILED FOR x64

7. All of the built in Avisynth functions should be working properly, as of now, you're limited to these UNLESS:

8. Write, modify, and compile your own 64bit versions of existing Avisynth filters, most are open source anyhow, you're only limited by the compiler
	-Intel's C++ compiler understands inline ASM and uses "Intel's Syntax" which is what VS2005 interprets when it hits an ASM block
	-The GNU C++ compiler also understands inline ASM, but uses AT&T syntax, making porting this way a bit more cumbersome
	-Re-write the inline assembler using standard assembly if needed, YASM (http://www.tortall.net/projects/yasm/) has full 64bit windows functionality 

9. Visit Squid80's homepage: http://members.optusnet.com.au/squid_80/
	-He did a lot of heavy lifting to get Avisynth 2.5.5 to compile and work WITHOUT inline asm in the first place (WOW)
	-He also has a collection of plugins already compiled for use with 64bit avisynth

10. Report any feedback, problems, questions, and concerns on the doom9 forum
	-http://forum.doom9.org/showthread.php?p=1374745#post1374745
	-If it's a bug, please provide a clip and sample script so I can recreate it

11. If there are any plugins you routinely use that you'd like to see supported in the 64bit version of Avisynth, please stop by the forum and let me know.  I will do my
	best to fulfill any feature requests, just keep in mind that I'm only human, and the only human actively compiling this source :)


----------------------------------------------------------------
Changes in version x64 1.01 
----------------------------------------------------------------

+ Added new resize code for all horizontal and vertical resizers that greatly increases their speed while mainting the original quality

+ BitBlt memory copy routines now use the larger SSE registers, producing appreciable speed gains

+ Bottlenecks in temporal soften have been alleviated somewhat by re-writing slower subroutines to use SSE instead of MMX

+ Minor code changes throughout the source should alleviate other speed issues

+ RGB colorspace conversions are back in place and function as they do in Avisynth32

+ SoundTouch updated to latest version with improved compatibility across all platforms

+ Assembly routines have been written to support full Win64 exception handling

+ Source compiled with newer Intel C++ compiler v11.1.056

+ Note: As I go, I've noticed some plugins will provice error information that is completely useless.  The culprit is usually a change in the avisynth.h header file used to compile the source.
	Please inform me of any odd issues you encounter along the way. 

----------------------------------------------------------------
Release Notes x64 version 1.0
----------------------------------------------------------------

+ All built in Avisynth filters are present in this release, syntax is exactly the same
	-No guarantee they all work correctly, I'm only one person and can't test every corner case of every aspect of the code.

+ SetMTMode(mode, thread) is supported, but not MT("command"), this is contained in another dll, for another day

+ Source was built with Intel C++ compiler v11.1.048

+ There are optimized code paths for all Intel processors from Netburst with EMT64 all the way to Core i3/i5/i7.  Any AMD processors supporting the same feature flags should work equivalently

+ Flags used for the compiler:
/c /O3 /Og /Oi /Ot /GT /Qipo /D "NDEBUG" /D "INC_OLE2" /D "STRICT" /D "WIN64" /D "_WIN64" /D "_AMD64_" /D "_CRT_SECURE_NO_DEPRECATE" 
/D "_MT" /D "_USRDLL" /D "AVISYNTH_C_EXPORTS" /D "_STATIC_CPPLIB" /D "ARCH_IS_64BIT" /D "ARCH_IS_X86_64" /D "_VC80_UPGRADE=0x0600" 
/D "_WINDLL" /D "_MBCS" /GF /EHsc /MT /GS- /fp:fast=2 /GR- /W2 /nologo /Zi /Qparallel /Quse-intel-optimized-headers 
/Qopenmp-link:static /QaxSSE2,SSE3,SSSE3,SSE4.1,SSE4.2

+ Options used for the linker:
/MANIFEST:NO /TLBID:1 /SUBSYSTEM:WINDOWS /LARGEADDRESSAWARE /OPT:REF /OPT:ICF /RELEASE /DYNAMICBASE 
/MERGE:"_TEXT64=.text" /MACHINE:X64 /DLL

----------------------------------------------------------------
Thanks yous
----------------------------------------------------------------

Thanks to turbojet for taking the time and interest to write an easy intaller for the registry settings.  

Thanks to Stephen R. Savage for his interest and continued input on the project.  His time and testing has made it much easier to ensure a smooth experience for all users
interested in using this port of the project and the accompanied filters.

I'd like to thank anyone who's contributed to the Avisynth project, from the original creators to the plugin developers to the script writers.  
It's a very interesting peice of code that has a ton of power when it comes to media editing.  I would really like to see it get more wide spread use.

I'd also like to thank the doom9 community in general, I've been lurking around those boards for the past few months and the vast majority of you have been very helpful.
It's a great source of information for anything media editing related and often the source for answers to seemingly impossible answers.

----------------------------------------------------------------
Why 64 bit? (This gets a little technical)
----------------------------------------------------------------

Short version:
More and faster access to large amounts of data.  The ability to utlize more memory than conventional 32 bit addressing allows.  These things come
in really handy for working with large amounts of video and audio data.  When running and application in 32 bit mode, these advantages are stripped 
from the programmer and user.

Long (technical) version:
The x86 instruction set has undergone a lot of revisions since its inception, each one striving to keep it relevant when compared to advances in
other architechtures.  Because it's ancestry is so antiquated, it has traditionally been VERY starved for registers, making frequent accesses to memory
the norm.

When AMD instroducted x86-64, they changed a long overdue, and very needed aspect of the x86 architecture itself.  They doubled the amount (and size) of
general purpose storage registers, as well as doubled the amount of 128 bit XMM registers (SIMD registers for SSE instructions).  They changed the function
calling convention in C++ so less data was passed on the stack, (for the most part) removed segmented addressing, and did away with some other antiquated
methods of doing things at the chip level.  The overall result was a nice restart for x86, while also maintaining backwards compatibility with the old
codebase that's existed for decades.

The problem is, running "old" 32 bit code still limits the program to 8 general purpose registers, 8 mmx registers, and 8 xmm registers.  It also maintains
segmented adressing and support for some other legacy features.  The only way to open up the extra registers is to run the application in 64bit mode natively.

The difference between 32 bit mode and 64 bit mode means that it's impossible for binaries compiled for x86 and x64 can't "talk" to each other.  The communication
channels are just too different.  The best way for avisynth scripts to take advantage of the speed increases afforded by 64bit media compressors is to have a native
64 bit version built.  

I started this version purely to occupy some of my free time, and because I was fascinated with Avisynth to begin with.  There's still optimization that can be done 
to the current assembly routines, and it lacks much of the plugin base.  Another downside is that the main project is still being developed for 32 bit platforms exclusively.
Avisynth 2.6 will be 32 bit, and I do not believe there are plans to create a 64 bit branch.  It would be nice for forward compatibility, but, converting ALL that code
is a monstrous effort, and if the payoff isn't worth it, then it was energy wasted.  I have some idea tjat Avisynth benefits from 64bit execution, but am unsure of the real speed gains. 
For now, I'm happy with my little side show here, and look forward to advances in the main branch of Avisynth.