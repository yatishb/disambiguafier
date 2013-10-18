disambiguafier
==============

To perform context sensitive spelling correction. The program is to detect spelling errors 
that result in valid words and suggest their suitable alternatives

Specifically, I will implement a program to determine whether a word w in a given 
confusion set { w1, w2 } should be disambiguated as w1 or w2. The task is formulated as a
supervised learning task from labeled training sentences. I will try to implement a
Bayesian classifier for context-sensitive spelling correction, making use of the naïve
Bayes assumption. The features used include:

(a) Surrounding words: Each word that appears in the sentence containing the confusable
word w is a feature. All surrounding words are converted to lowercase, and stop words
and punctuation symbols are removed.
(b) Collocations: A collocation Ci,j is an ordered sequence of words in the local, narrow
context of the confusable word w. Offsets i and j denote the starting and ending positions
(relative to w) of the sequence, where a negative (positive) offset refers to a word to its
left (right). Each collocation string spanning positions i to j is a feature. You get to decide
on the list of collocations (i.e., what pairs of offsets) to use.
