#pragma once

#ifndef NDEBUG
#include <iostream>
#define DEBUG_LOG(x) std::cerr << x << std::endl
#else
#define DEBUG_LOG(x)
#endif