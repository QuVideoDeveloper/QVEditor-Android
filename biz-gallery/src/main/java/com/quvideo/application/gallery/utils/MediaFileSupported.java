package com.quvideo.application.gallery.utils;

public class MediaFileSupported {
  private final static String[] SUPPORTED_MUSICS_EXT = { "MP3", "M4A", "AAC" };
  private final static String[] SUPPORTED_VIDEOS_EXT =
      { "MP4", "3GP", "3G2", "M4V", "3GPP", "MOV" };
  private final static String[] SUPPORTED_PHOTOS_EXT = { "JPG", "BMP", "JPEG", "PNG", "GIF" };

  //refer to http://linglong117.blog.163.com/blog/static/27714547200991410139472/
  private final static String[] SUPPORTED_MUSICS_MIMETYPE = {
      "audio/3gpp",// 	3GPP Media	QuickTime
      "audio/3gpp2",// 	3GPP2 Media	QuickTime
      "audio/aac",// 	AAC Audio	QuickTime
      "audio/mp3",// 	MPEG Layer 3 Audio	QuickTime Crescendo Player
      "audio/mp4",// 	MPEG-4 Media	QuickTime
      "audio/mpeg",// 	MPEG Audio	QuickTime Crescendo Player
      "audio/mpeg3",// 	MPEG Layer 3 Audio 2	QuickTime
      "audio/mpg",// 	MPEG Audio 2	Crescendo Player
      "audio/x-aac",// 	AAC Audio	QuickTime
      "audio/x-m4a",// 	AAC Audio	QuickTime
      "audio/x-mp3",// 	MPEG Layer 3 Audio	QuickTime Crescendo Player
      "audio/x-mpeg",// 	MPEG Audio	QuickTime
      "audio/x-mpeg3",// 	MPEG Layer 3 Audio 2	QuickTime
      "audio/x-mpg",// 	MPEG Audio 2	Crescendo Player

	/*		
		"audio/aiff",// 	AIFF Audio	QuickTime
		"audio/amr",// 	AMR Audio	QuickTime
		"audio/basic",// 	uLaw/AU Audio	QuickTime
		"audio/it",// 	Impulse Tracker Modules	MODPlug
		"audio/mid",// 	MIDI Audio	QuickTime Crescendo Player
		"audio/midi",// 	MIDI Audio	QuickTime Crescendo Player Beatnik Player
		"audio/mod",// 	ProTracker Modules	MODPlug
		"audio/mpegurl",// 	MPEG Audio Playlist	QuickTime
		"audio/rmf",// 	
		"audio/s3m",// 	ScreamTracker Modules	MODPlug
		"audio/songsafe",// 	Crescendo SongSafe	Crescendo Player
		"audio/vnd.qcelp",// 	QUALCOMM PureVoice Audio	QuickTime
		"audio/wav",// 	WAVE Audio	QuickTime
		"audio/x-aiff",// 	AIFF Audio	QuickTime
		"audio/x-caf",// 	CAF Audio	QuickTime
		"audio/x-gsm",// 	GSM Audio	QuickTime
		"audio/x-m4b",// 	AAC Audio Book	QuickTime
		"audio/x-m4p",// 	AAC Audio (Protected)	QuickTime
		"audio/x-mid",// 	MIDI Audio 2	Crescendo Player
		"audio/x-midi",// 	MIDI Audio	QuickTime
		"audio/x-mod",// 	
		"audio/x-mpegurl",// 	MPEG Audio Playlist	QuickTime
		"audio/x-ms-wax",// 	Windows Media (Metafile)	Windows Media Player
		"audio/x-ms-wma",// 	Windows Media (Metafile)	Windows Media Player
		"audio/x-pn-realaudio-plugin",//RealPlayer
		"audio/x-rmf", //Beatnik Player
		"audio/x-s3m",// 	ScreamTracker Modules	MODPlug
		"audio/x-sd2",// 	Sound Designer II Audio	QuickTime
		"audio/x-songsafe",// 	Crescendo SongSafe	Crescendo Player
		"audio/x-wav",// 	WAVE Audio	QuickTime Yamaha MIDPLUG for XG
		"audio/x-zipped-it",// 	Zipped IT Module	MODPlug
		"audio/x-zipped-mod",// 	Zipped Module	MODPlug audio/xm
	*/
  };

  private final static String[] SUPPORTED_VIDEOS_MIMETYPE = {
      "video/mp4",//	MPEG-4 Media	QuickTime
      "video/mpeg",//	MPEG-4 Media	QuickTime
      "video/3gpp",//	3GPP Media	QuickTime
      "video/3gpp2",//	3GPP2 Media	QuickTime
      "video/x-m4v",//	Video (Protected)	QuickTime
      "video/x-mpeg",//	MPEG Media	QuickTime
		/*
		"video/avi",//	Video For Windows (AVI)	QuickTime
		"video/divx",//	DivX Video	DivX Web Player
		"video/flc",//	AutoDesk Animator (FLC)	QuickTime
		"video/msvideo",//	Video for Windows	QuickTime
		"video/quicktime",//	QuickTime Movie	QuickTime VLC Media Player
		"video/sd-video",//	SD Video	QuickTime
		"video/x-dv",//	Digital Video (DV)	QuickTime
		"video/x-ivf",//	Indeo Video	Ligos Indeo
		"video/x-ms-asf",//	Windows Media (Legacy Content)	Windows Media Player
		"video/x-ms-asf",// -plugin	Windows Media (Legacy Content)	Windows Media Player
		"video/x-ms-wm",//	Windows Media (Reserved)	Windows Media Player
		"video/x-ms-wmv",//	Windows Media (Audio and/or Video)	Windows Media Player
		"video/x-ms-wmx",//	Windows Media (Reserved)	Windows Media Player
		"video/x-msvideo",//
		*/
  };

  private final static String[] SUPPORTED_PHOTOS_MIMETYPE = {
      "image/jpeg",//	JPEG Image	Handled Internally 4
      "image/jpg",//	JPEG Image	Handled Internally 4
      "image/png",//	PNG Image	Handled Internally 4
      "image/bmp",//	Windows or OS/2 Bitmap Image	Handled Internally 4
      "image/gif",//	GIF Image	Handled Internally
      "image/jpe",//	JPEG Image 2	Prizm Viewer
      "image/jpeg2000",//	JPEG 2000 Image 2	LizardTech Express View
      "image/jpeg2000-image",//	JPEG 2000 Image 2	QuickTime
      "image/x-bmp",//	Windows or OS/2 Bitmap Image 2	QuickTime
      "image/x-png",//	PNG Image 2	Handled Internally 4
	/*
		"image/c4",//	JEDMICS C4	CPC Lite
		"image/cals",//	CALS Type 1 Raster	CPC Lite
		"image/cpi",//	CPC	CPC Lite
		"image/dib",//	Device Independent Bitmap	Prizm Viewer
		"image/djvu",//	DJVU File	DjVu Plugin
		"image/j2c",//	J2C Image	Morgan JPEG 2000 Plugin
		"image/j2k",//	Lurawave JP2 Stream	Algo Vision Luratech
		"image/jp2",//	JPEG 2000 Image	Morgan JPEG 2000 Plugin
		"image/jpc",//	Lurawave .jp2 Stream	Algo Vision Luratech
		"image/jpx",//	JPX Image	Morgan JPEG 2000 Plugin
		"image/pbm",//	Portable Anymap	CPC Lite
		"image/pict",//	PICT Image	QuickTime
		"image/svg+xml",//	SVG Image	Handled Internally 5
		"image/svg-xml",//	SVG Image 2	Adobe SVG Viewer
		"image/tif",//	TIFF Image 2	Accel ViewTIFF
		"image/tiff",//	TIFF Image	AlternaTIFF
		"image/vnd.adobe.svg+xml",//	SVG Image (Adobe)	Adobe SVG Viewer
		"image/vnd.djvu",//	DJVU File	DjVu Plugin
		"image/vnd.dwf",//	Drawing Web Format	WHIP! Viewer
		"image/vnd.fpx",//	FlashPix File	Zoom Image Viewer
		"image/vnd.microsoft.icon",//	Microsoft Icon Image	Handled Internally
		"image/vnd.swiftview-cals",//	Cals Image (SwiftView)	SwiftView
		"image/vnd.swiftview-pcx",//	PCX Image (SwiftView)	SwiftView
		"image/x-cal",//	
		"image/x-cif",//	CVISION Image Format	CVista Viewer
		"image/x-cmx",//	
		"image/x-cpi",//	
		"image/x-dcx",//	
		"image/x-dejavu",//	DJVU File	DjVu Plugin
		"image/x-djvu",//	DJVU File	DjVu Plugin
		"image/x-doc-wavelet",//	LuraDocument	Algo Vision Luratech
		"image/x-fastbid2-fbs",//	FastBid Sheet	FastBid
		"image/x-icon",//	Microsoft Icon Image	Handled Internally
		"image/x-img",//	
		"image/x-iw44",//	IW44 File	DjVu Plugin
		"image/x-jbig2",//	JBIG2 Image	CVista Viewer
		"image/x-jpeg2000-image",//	JPEG 2000 Image 2	QuickTime
		"image/x-macpaint",//	MacPaint Image	QuickTime
		"image/x-mrsid-image",//	MrSID File	Lizardtech Express View
		"image/x-photoshop",//	Photoshop Image	QuickTime
		"image/x-pict",//	PICT Image	QuickTime
		"image/x-quicktime",//	QuickTime Image	QuickTime
		"image/x-sgi",//	SGI Image	QuickTime
		"image/x-targa",//	TGA Image	QuickTime
		"image/x-tiff",//	TIFF Image 2	QuickTime
		"image/x-wavelet",//	LuraWave	Algo Vision Luratech
		"image/x-xbitmap",//	XBM Image	Handled Internally
		"image/x.djvu",//	DJVU File	DjVu Plugin
	*/
  };

  public static String[] getSupportPhotosExt() {
    return SUPPORTED_PHOTOS_EXT;
  }

  public static String[] getSupportVideosExt() {
    return SUPPORTED_VIDEOS_EXT;
  }

  public static String[] getSupportMusicsExt() {
    return SUPPORTED_MUSICS_EXT;
  }

  public static String[] getSupportPhotosMimeType() {
    return SUPPORTED_PHOTOS_MIMETYPE;
  }

  public static String[] getSupportVideosMimeType() {
    return SUPPORTED_VIDEOS_MIMETYPE;
  }

  public static String[] getSupportMusicsMimeType() {
    return SUPPORTED_MUSICS_MIMETYPE;
  }
}
