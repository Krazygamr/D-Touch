#include "sigc++/sigc++.h"
#include "ControlSuper.h"
#include "GLRect.h"
#include "PointF.h"
#include "OpenGLUtils.h"

#ifndef _AnalogStick_H_
#define _AnalogStick_H_


namespace touchcontrols
{

class AnalogStick : public ControlSuper
{
	bool pressed;

	int id;

	std::string image;

	GLuint glTex;

	GLRect glRect;

	PointF value;
	PointF anchor;
	PointF fingerPos;


public:
	sigc::signal<void, float,float> signal_move;

	AnalogStick(std::string tag,RectF pos,std::string image_filename);

	bool processPointer(int action, int pid, float x, float y);

	bool drawGL();

	bool initGL();

	void updateSize();

	void saveXML(TiXmlDocument &doc);

	void loadXML(TiXmlDocument &doc);
private:

	void reset();
	void calcNewValue();
	void doUpdate(PointF value);
};

}

#endif
