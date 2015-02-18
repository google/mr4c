#include "J2kDecoder.h"



J2kImage::J2kImage( opj_image_t * image ) : m_image( image ) {
}


J2kImage::~J2kImage() {
    opj_image_destroy(m_image);
}

J2kImage::DimType J2kImage::getDimensions() const {
    opj_image_comp_t comp = m_image->comps[ 0 ];
    return DimType( comp.w, comp.h );
}

int * J2kImage::getPixels() const {
    return m_image->comps[ 0 ].data;
}

J2kImageDecoder::J2kImageDecoder() {
    opj_set_default_decoder_parameters(&parameters);
    
    dinfo = opj_create_decompress(CODEC_J2K);
    
    opj_setup_decoder(dinfo, &parameters);        
}

J2kImage * J2kImageDecoder::getImage( char * data, size_t len ) {
    opj_image_t *image;
    
    opj_cio_t *cio = opj_cio_open((opj_common_ptr)dinfo, 
                                  reinterpret_cast<unsigned char*>(data), len);
    
    image = opj_decode(dinfo, cio);
    opj_cio_close(cio);
    return new J2kImage( image );
}

J2kImageDecoder::~J2kImageDecoder() {
    opj_destroy_decompress(dinfo);
}


