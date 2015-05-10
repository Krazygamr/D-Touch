#include "TouchPad.h"
#include "TouchControlsConfig.h"

using namespace touchcontrols;

TouchPad::TouchPad(std::string tag,RectF pos,int image_id):
																						ControlSuper(tag,pos)
{
	imageId = image_id;
	id = -1;
	glRect.resize(controlPos.right - controlPos.left, controlPos.bottom - controlPos.top);
};


bool TouchPad::processPointerDown(int pid, float x, float y)
{
	if (id == -1) //Only process if not active
	{
		if (controlPos.contains(x, y))
		{
			id = pid;
			last.x = x;
			last.y = y;
			anchor.x = x;
			anchor.y = y;
			return true;
		}
	}
	return false;
};

bool TouchPad::processPointerUp(int pid, float x, float y)
{
	if (id == pid)
	{
		reset();
		return true;
	}
	return false;
};

bool TouchPad::processMove(int pid, float x, float y)
{
	if (pid == id) //Finger already down
	{
		fingerPos.x = x;
		fingerPos.y = y;
		calcNewValue();
	}
};

bool TouchPad::drawGL()
{
	drawRect(imageId,controlPos.left,controlPos.top,glRect);
}

void TouchPad::reset()
{
	id = -1;
	value.x = 0;
	value.y = 0;
	doUpdate(value);

}

void TouchPad::calcNewValue()
{
	float dx = last.x - fingerPos.x;
	float dy = last.y - fingerPos.y;
	value.x = dx;
	value.y = dy;
	last.x =  fingerPos.x;
	last.y = fingerPos.y;

	doUpdate(value);

}

void TouchPad::doUpdate(PointF value)
{
	LOGTOUCH("x = %f y = %f",value.x,value.y);
	signal_move.emit(value.x,value.y);
}
