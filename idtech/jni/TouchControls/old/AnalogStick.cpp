#include "AnalogStick.h"
#include "TouchControlsConfig.h"

using namespace touchcontrols;

AnalogStick::AnalogStick(std::string tag,RectF pos,std::string image_filename):
																		ControlSuper(tag,pos)
{
	image = image_filename;
	id = -1;
	updateSize();
};

void AnalogStick::updateSize()
{
	glRect.resize(controlPos.right - controlPos.left, controlPos.bottom - controlPos.top);
}

bool AnalogStick::processPointer(int action, int pid, float x, float y)
{
	if (action == P_DOWN)
	{
		if (id == -1) //Only process if not active
		{
			if (controlPos.contains(x, y))
			{
				id = pid;
				anchor.x = x;
				anchor.y = y;
				fingerPos.x = x;
				fingerPos.y = y;
				return true;
			}
		}
		return false;
	}
	else if (action == P_UP)
	{
		if (id == pid)
		{
			reset();
			return true;
		}
		return false;
	}
	else if(action == P_MOVE)
	{
		if (pid == id) //Finger already down
		{
			fingerPos.x = x;
			fingerPos.y = y;
			calcNewValue();
			return true;
		}
		return false;
	}
}



bool AnalogStick::initGL()
{
	int x,y;
	glTex = loadTextureFromPNG(image,x,y);
}

bool AnalogStick::drawGL()
{
	drawRect(glTex,controlPos.left,controlPos.top,glRect);
}

void AnalogStick::reset()
{
	id = -1;
	value.x = 0;
	value.y = 0;
	//Set to middle for draw
	fingerPos.x = controlPos.left + controlPos.width()/2;
	fingerPos.y = controlPos.top + controlPos.height()/2;

	doUpdate(value);

}

void AnalogStick::calcNewValue()
{
	float dx = anchor.x - fingerPos.x;
	float dy = anchor.y - fingerPos.y;
	value.x = dx;
	value.y = dy;
	if (value.x > 1)
		value.x = 1;
	else if (value.x < -1)
		value.x = -1;

	if (value.y > 1)
		value.y = 1;
	else if (value.y < -1)
		value.y = -1;

	doUpdate(value);

}

void AnalogStick::doUpdate(PointF value)
{
	//LOGTOUCH("x = %f y = %f",value.x,value.y);
	signal_move.emit(value.x,value.y);
}

void AnalogStick::saveXML(TiXmlDocument &doc)
{

}

void AnalogStick::loadXML(TiXmlDocument &doc)
{

}
