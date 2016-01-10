#!/bin/bash

shopt -s globstar

for _file in **/*.java; do
		ed -s "${_file}" <<EOF
0a
//
// This work is licensed under the Creative Commons
// Attribution-ShareAlike 3.0 Unported License. To view a copy of this
// license, visit http://creativecommons.org/licenses/by-sa/3.0/
//

.
w
EOF
		done
