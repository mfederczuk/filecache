/*
 * Copyright 2021 Michael Federczuk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.mfederczuk.filecache


private fun String.getPrefixToAdd(prefix: String): String {
	var commonPartLength = prefix.length

	while(commonPartLength > 0) {
		val regionMatches =
			this.regionMatches(
				thisOffset = 0,
				other = prefix,
				length = commonPartLength,
				otherOffset = prefix.length - commonPartLength
			)

		if(regionMatches) {
			return prefix.substring(0, prefix.length - commonPartLength)
		}

		--commonPartLength
	}

	return prefix
}


internal fun String.withPrefix(prefix: String): String {
	val prefixToAdd = this.getPrefixToAdd(prefix)

	return buildString(prefixToAdd.length + this.length) {
		append(prefixToAdd)
		append(this@withPrefix)
	}
}

internal fun String.withSuffix(suffix: String): String {
	return this.reversed().withPrefix(suffix.reversed()).reversed()
}

internal fun String.withPrefixAndSuffix(prefix: String, suffix: String): String {
	val prefixToAdd = this.getPrefixToAdd(prefix)
	val thisWithSuffix =  this.withSuffix(suffix)

	return buildString(prefixToAdd.length + thisWithSuffix.length) {
		append(prefixToAdd)
		append(thisWithSuffix)
	}
}
