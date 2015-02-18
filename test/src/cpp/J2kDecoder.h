#ifndef __MR4CREF_J2KDECODER_H__
#define __MR4CREF_J2KDECODER_H__

#include <openjpeg.h>
#include <utility>
#include <cstring>

class J2kImageDecoder;

class J2kImage {
    opj_image_t * m_image;
    friend class J2kImageDecoder;

protected:
    J2kImage( opj_image_t * image );

public:
    typedef std::pair<unsigned int, unsigned int> DimType;

    ~J2kImage();

    DimType getDimensions() const;

    int * getPixels() const;
};

class J2kImageDecoder {

    opj_dparameters_t parameters;
    opj_dinfo_t *     dinfo;

public:
    J2kImageDecoder();

    J2kImage * getImage( char * data, size_t len );

    ~J2kImageDecoder();
 
};



#endif  // __MR4CREF_J2KDECODER_H__
