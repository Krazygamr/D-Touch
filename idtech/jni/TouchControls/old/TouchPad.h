#include "sigc++/sigc++.h"
#include "ControlSuper.h"
#include "GLRect.h"
#include "PointF.h"
#include "OpenGLUtils.h"

#ifndef _TouchPad_H_
#define _TouchPad_H_


namespace touchcontrols
{

class TouchPad : public ControlSuper
{
	bool pressed;

	int id;

	int imageId;

	PointF value;
	PointF last;
	PointF fingerPos;
	PointF anchor;

	GLRect glRect;

public:
	sigc::signal<void, float,float> signal_move;

	TouchPad(std::string tag,RectF pos,int image_id);

	bool processPointerDown(int pid, float x, float y);

	bool processPointerUp(int pid, float x, float y);

	bool processMove(int pid, float x, float y);

	bool drawGL();

private:

	void reset();
	void calcNewValue();
	void doUpdate(PointF value);
};

}

#endif
